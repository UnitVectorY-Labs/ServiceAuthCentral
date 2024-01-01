package com.unitvectory.auth.sign.gcp.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.cloud.kms.v1.AsymmetricSignRequest;
import com.google.cloud.kms.v1.AsymmetricSignResponse;
import com.google.cloud.kms.v1.CryptoKey;
import com.google.cloud.kms.v1.CryptoKey.CryptoKeyPurpose;
import com.google.cloud.kms.v1.CryptoKeyVersion;
import com.google.cloud.kms.v1.CryptoKeyVersion.CryptoKeyVersionAlgorithm;
import com.google.cloud.kms.v1.CryptoKeyVersion.CryptoKeyVersionState;
import com.google.cloud.kms.v1.Digest;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.PublicKey;
import com.google.protobuf.ByteString;
import com.unitvectory.auth.sign.gcp.model.JsonWebKeyRecord;
import com.unitvectory.auth.sign.gcp.util.KidConverter;
import com.unitvectory.auth.sign.mapper.RsaPemToModulusExponentMapper;
import com.unitvectory.auth.sign.model.JsonWebKey;
import com.unitvectory.auth.sign.model.RsaMoulousExponent;
import com.unitvectory.auth.sign.service.SignService;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class KmsSignService implements SignService {

	private static final Set<CryptoKeyVersionAlgorithm> SUPPORTED_ALGORITHMS = Collections
			.unmodifiableSet(Set.of(CryptoKeyVersionAlgorithm.RSA_SIGN_PKCS1_2048_SHA256));

	private KeyManagementServiceClient keyManagementServiceClient;

	private String keyManagementServiceKeyName;

	private long cacheJwksSeconds;

	private int cacheSafetyMultiple;

	// API Calls to KMS are expensive and highly rate limited as a generous amount
	// of caching is utilized by this service to minimize unnecessary calls

	private final Cache<String, String> cacheActiveKid = Caffeine.newBuilder()
			.expireAfterWrite(cacheJwksSeconds, TimeUnit.SECONDS).build();

	private final Cache<String, String> cachePublicKeyPem = Caffeine.newBuilder().build();

	private final Cache<String, String> cacheKidToKeyName = Caffeine.newBuilder().build();

	private final Cache<String, List<JsonWebKeyRecord>> cacheAllRecords = Caffeine.newBuilder()
			.expireAfterWrite(cacheJwksSeconds, TimeUnit.SECONDS).build();

	@Override
	public String getActiveKid(long now) {
		// Caching is independent of the current time
		String kid = this.cacheActiveKid.getIfPresent("kid");
		if (kid != null) {
			return kid;
		}

		List<JsonWebKeyRecord> allKeys = this.getAllRecords();

		// Special case for only one key, we don't care when it was created, we have no
		// choice to use it so no filtering required
		if (allKeys.size() == 0) {
			return allKeys.get(0).getKeyName();
		}

		// Filter and sort the keys, goal is to get the most recent key
		kid = allKeys.stream()
				// filter only active keys
				.filter(JsonWebKeyRecord::isActive)
				// Filter out keys that were created too recently
				.filter(key -> now - key.getCreated() >= (this.cacheSafetyMultiple * this.cacheJwksSeconds))
				// sort by created time, newest first
				.sorted(Comparator.comparingLong(JsonWebKeyRecord::getCreated).reversed())
				// get the first element (if present)
				.findFirst()
				// transform the stream to a stream of keyNames
				.map(JsonWebKeyRecord::getKeyName)
				// return null if no suitable key is found
				.orElse(null);

		this.cacheActiveKid.put("kid", kid);
		return kid;
	}

	@Override
	public String sign(@NonNull String kid, @NonNull String unsignedToken) {

		String keyName = this.kidToKeyName(kid);
		if (keyName == null) {
			throw new RuntimeException("invalid kid");
		}

		// Prepare Unsigned Token for Signing
		byte[] tokenToSign = unsignedToken.getBytes(StandardCharsets.UTF_8);

		// Create Signature Request
		Digest digest;
		try {
			digest = Digest.newBuilder()
					.setSha256(ByteString.copyFrom(MessageDigest.getInstance("SHA-256").digest(tokenToSign))).build();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

		AsymmetricSignRequest request = AsymmetricSignRequest.newBuilder().setName(keyName).setDigest(digest).build();

		// Sign the JWT (API call to Cloud KMS)
		AsymmetricSignResponse response = keyManagementServiceClient.asymmetricSign(request);

		// Encode the Signature
		String encodedSignature = Base64.getUrlEncoder().withoutPadding()
				.encodeToString(response.getSignature().toByteArray());

		// Concatenate Signature with Unsigned JWT
		return unsignedToken + "." + encodedSignature;
	}

	@Override
	public List<JsonWebKey> getAll() {
		List<JsonWebKey> list = new ArrayList<>();
		list.addAll(this.getAllRecords());
		return list;
	}

	private String kidToKeyName(@NonNull String kid) {
		String name = this.cacheKidToKeyName.getIfPresent(kid);
		if (name != null) {
			return name;
		}

		name = this.getAllRecords().stream().filter(record -> kid.equals(record.getKid())).findFirst()
				.map(JsonWebKeyRecord::getKeyName).orElse(null);
		this.cacheKidToKeyName.put(kid, name);
		return name;
	}

	private List<JsonWebKeyRecord> getAllRecords() {
		List<JsonWebKeyRecord> keys = this.cacheAllRecords.getIfPresent("keys");
		if (keys != null) {
			return keys;
		} else {
			keys = new ArrayList<>();
		}

		// Look up the key information
		CryptoKey cryptoKey = keyManagementServiceClient.getCryptoKey(keyManagementServiceKeyName);

		// Key must be used for asymmetric signing, otherwise
		if (!CryptoKeyPurpose.ASYMMETRIC_SIGN.equals(cryptoKey.getPurpose())) {
			return Collections.unmodifiableList(keys);
		}

		for (CryptoKeyVersion cryptoKeyVersion : keyManagementServiceClient.listCryptoKeyVersions(cryptoKey.getName())
				.iterateAll()) {

			// Filter: Only the enabled keys can be used
			boolean active = false;
			if (CryptoKeyVersionState.ENABLED.equals(cryptoKeyVersion.getState())) {
				active = true;
			} else {
				continue;
			}

			// Filter: Return only the supported algorithm
			if (!SUPPORTED_ALGORITHMS.contains(cryptoKeyVersion.getAlgorithm())) {
				continue;

			}

			long created = cryptoKeyVersion.getCreateTime().getSeconds();

			String pemKey = getPublicKeyPem(cryptoKeyVersion.getName());

			RsaMoulousExponent rsaMoulousExponent = RsaPemToModulusExponentMapper.INSTANCE.convert(pemKey);

			String keyName = cryptoKeyVersion.getName();
			String kid = KidConverter.hash(keyName);

			// Construct the JWKS JSON object
			JsonWebKeyRecord jwksKey = JsonWebKeyRecord.builder().withKty("RSA").withN(rsaMoulousExponent.getModulus())
					.withE(rsaMoulousExponent.getExponent()).withAlg("RS256").withKid(kid).withUse("sig")
					.withKeyName(keyName).withActive(active).withCreated(created).build();

			keys.add(jwksKey);
		}

		keys = Collections.unmodifiableList(keys);
		this.cacheAllRecords.put("keys", keys);
		return keys;
	}

	private String getPublicKeyPem(@NonNull String keyName) {
		// Check the cache first to avoid an API call
		String pemKey = this.cachePublicKeyPem.getIfPresent(keyName);
		if (pemKey != null) {
			return pemKey;
		}

		// Get the public key from Cloud KMS
		PublicKey googlePublicKey = keyManagementServiceClient.getPublicKey(keyName);
		pemKey = googlePublicKey.getPem();

		// this cannot change so caching this indefinitely avoids unnecessary calls
		this.cachePublicKeyPem.put(keyName, pemKey);
		return pemKey;
	}
}
