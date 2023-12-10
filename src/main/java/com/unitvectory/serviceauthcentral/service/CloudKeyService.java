package com.unitvectory.serviceauthcentral.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.cloud.kms.v1.CryptoKey;
import com.google.cloud.kms.v1.CryptoKey.CryptoKeyPurpose;
import com.google.cloud.kms.v1.CryptoKeyVersion;
import com.google.cloud.kms.v1.CryptoKeyVersion.CryptoKeyVersionAlgorithm;
import com.google.cloud.kms.v1.CryptoKeyVersion.CryptoKeyVersionState;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.PublicKey;
import com.unitvectory.serviceauthcentral.dto.JwksKey;

@Service
public class CloudKeyService {

	private static final Set<CryptoKeyVersionAlgorithm> SUPPORTED_ALGORITHMS = Collections
			.unmodifiableSet(Set.of(CryptoKeyVersionAlgorithm.RSA_SIGN_PKCS1_2048_SHA256));

	@Autowired
	private int cacheJwksHours;

	@Autowired
	private CryptoService cryptoService;

	@Autowired
	private KeyManagementServiceClient keyManagementServiceClient;

	@Autowired
	private String keyManagementServiceKeyName;

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
