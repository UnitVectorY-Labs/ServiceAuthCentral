package com.unitvectory.serviceauthcentral.repository.key;

import java.util.List;

import com.auth0.jwk.Jwk;
import com.unitvectory.serviceauthcentral.dto.CachedJwk;

public interface KeySetRepository {

	CachedJwk getKey(String url, String keyId) throws Exception;

	List<CachedJwk> getKeys(String url) throws Exception;

	void saveKey(String url, Jwk jwk) throws Exception;

	void saveNoKey(String url, String keyId) throws Exception;
}
