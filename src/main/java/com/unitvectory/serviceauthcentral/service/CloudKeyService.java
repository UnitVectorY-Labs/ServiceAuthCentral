package com.unitvectory.serviceauthcentral.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;

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
import com.unitvectory.serviceauthcentral.dto.JwksKey;

public class CloudKeyService implements KeyService {

	private static final Set<CryptoKeyVersionAlgorithm> SUPPORTED_ALGORITHMS = Collections
			.unmodifiableSet(Set.of(CryptoKeyVersionAlgorithm.RSA_SIGN_PKCS1_2048_SHA256));

	@Value("${serviceauthcentral.cache.jwks.hours}")
	private int cacheJwksHours;

	@Autowired
	private CryptoService cryptoService;

	@Autowired
	private KeyManagementServiceClient keyManagementServiceClient;

	@Autowired
	private TimeService timeService;

	@Autowired
	private String keyManagementServiceKeyName;

	@Cacheable("activeKeyCache")
	public String getActiveKeyName() {
		List<JwksKey> allKeys = this.getAllKeys();

		// Special case for only one key, we don't care when it was created, we have no
		// choice to use it so no filtering required
		if (allKeys.size() == 0) {
			return allKeys.get(0).getKeyName();
		}

		long now = timeService.getCurrentTimeSeconds();
		long threshold = cacheJwksHours * 60 * 60;

		// Filter and sort the keys, goal is to get the most recent key
		return allKeys.stream()
				// filter only active keys
				.filter(JwksKey::isActive)
				// Filter out keys that were created too recently
				.filter(key -> now - key.getCreated() >= 2 * threshold)
				// sort by created time, newest first
				.sorted(Comparator.comparingLong(JwksKey::getCreated).reversed())
				// get the first element (if present)
				.findFirst()
				// transform the stream to a stream of keyNames
				.map(JwksKey::getKeyName)
				// return null if no suitable key is found
				.orElse(null);

	}

	public String signJwt(String keyName, String unsignedToken) {

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

	@Cacheable("jwksCache")
	public List<JwksKey> getAllKeys() {
		List<JwksKey> keys = new ArrayList<>();

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

			JwksKey jwksKey = this.cryptoService.convertRsaPublicKey(cryptoKeyVersion.getName(), pemKey, "RS256",
					active, created);
			keys.add(jwksKey);
		}

		return Collections.unmodifiableList(keys);
	}

	@Cacheable("publicKeyCache")
	private String getPublicKeyPem(String keyName) {

		// Get the public key from Cloud KMS, this cannot change so caching this
		// indefinitely avoids this API call unnecessarily
		PublicKey googlePublicKey = keyManagementServiceClient.getPublicKey(keyName);
		String pemKey = googlePublicKey.getPem();
		return pemKey;
	}

}
