package com.unitvectory.serviceauthcentral.repository;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Autowired;

import com.auth0.jwk.Jwk;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.common.hash.Hashing;
import com.unitvectory.serviceauthcentral.dto.CachedJwk;
import com.unitvectory.serviceauthcentral.service.TimeService;

public class FirestoreKeySetRepository implements KeySetRepository {

	@Autowired
	private Firestore firestore;

	@Autowired
	private TimeService timeService;

	@Nonnull
	private String getId(String url, String id) {
		String urlHash = Hashing.sha256().hashString(url, StandardCharsets.UTF_8).toString();
		String idHash = Hashing.sha256().hashString(id, StandardCharsets.UTF_8).toString();
		return Hashing.sha256().hashString(urlHash + idHash, StandardCharsets.UTF_8).toString();
	}

	@Override
	public void saveNoKey(String url, String keyId) throws Exception {
		if (url == null) {
			throw new IllegalArgumentException("url is required");
		}

		if (keyId == null) {
			throw new IllegalArgumentException("keyId is required");
		}

		CachedJwk cachedJwk = new CachedJwk(url, keyId,
				Instant.ofEpochSecond(this.timeService.getCurrentTimeSeconds()));

		String id = getId(url, keyId);
		firestore.collection("keys").document(id).set(cachedJwk.toMap()).get();
	}

	@Override
	public void saveKey(String url, Jwk jwk) throws Exception {
		if (url == null) {
			throw new IllegalArgumentException("url is required");
		}

		if (jwk == null) {
			throw new IllegalArgumentException("jwk is required");
		}

		CachedJwk cachedJwk = new CachedJwk(url, jwk.getId(),
				Instant.ofEpochSecond(this.timeService.getCurrentTimeSeconds()), jwk);

		String id = getId(url, jwk.getId());
		firestore.collection("keys").document(id).set(cachedJwk.toMap()).get();
	}

	@Override
	public CachedJwk getKey(String url, String keyId) throws Exception {
		if (url == null) {
			throw new IllegalArgumentException("url is required");
		}

		if (keyId == null) {
			throw new IllegalArgumentException("keyId is required");
		}

		String id = getId(url, keyId);
		DocumentSnapshot document = firestore.collection("keys").document(id).get().get();
		if (document.exists()) {
			Map<String, Object> map = document.getData();
			return new CachedJwk(map);
		} else {
			return null;
		}
	}
}