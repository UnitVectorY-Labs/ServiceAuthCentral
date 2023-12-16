package com.unitvectory.serviceauthcentral.repository;

import com.auth0.jwk.Jwk;
import com.unitvectory.serviceauthcentral.dto.CachedJwk;

public interface KeySetRepository {

	CachedJwk getKey(String url, String keyId) throws Exception;

	void saveKey(String url, Jwk jwk) throws Exception;

	void saveNoKey(String url, String keyId) throws Exception;
}
