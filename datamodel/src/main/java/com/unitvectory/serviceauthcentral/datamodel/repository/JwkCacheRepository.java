package com.unitvectory.serviceauthcentral.datamodel.repository;

import java.util.List;

import com.unitvectory.serviceauthcentral.datamodel.model.CachedJwk;

public interface JwkCacheRepository {

	void cacheJwk(String url, CachedJwk jwk, long ttl);

	void cacheJwkAbsent(String url, String kid, long ttl);

	List<CachedJwk> getJwks(String url);

	CachedJwk getJwk(String url, String kid);
}
