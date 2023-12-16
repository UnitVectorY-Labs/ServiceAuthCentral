package com.unitvectory.serviceauthcentral.repository.key;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.auth0.jwk.Jwk;
import com.unitvectory.serviceauthcentral.dto.CachedJwk;
import com.unitvectory.serviceauthcentral.service.time.TimeService;

public class MockedKeySetRepository implements KeySetRepository {

	@Autowired
	private TimeService timeService;

	private Map<String, CachedJwk> keyMap = new HashMap<>();

	@Override
	public CachedJwk getKey(String url, String keyId) throws Exception {
		return keyMap.get(url + ":" + keyId);
	}

	@Override
	public void saveKey(String url, Jwk jwk) throws Exception {
		keyMap.put(url + ":" + jwk.getId(),
				new CachedJwk(url, jwk.getId(), Instant.ofEpochSecond(timeService.getCurrentTimeSeconds()), jwk));
	}

	@Override
	public void saveNoKey(String url, String keyId) throws Exception {
		keyMap.put(url + ":" + keyId,
				new CachedJwk(url, keyId, Instant.ofEpochSecond(timeService.getCurrentTimeSeconds())));
	}

}
