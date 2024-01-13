package com.unitvectory.auth.datamodel.repository;

import java.util.List;

import com.unitvectory.auth.datamodel.model.CachedJwk;

/**
 * Interface for repository handling the caching of JSON Web Keys (JWK).
 */
public interface JwkCacheRepository {

	/**
	 * Caches a JSON Web Key (JWK) with the specified URL and time-to-live (TTL).
	 *
	 * @param url the URL where the JWK was retrieved from.
	 * @param jwk the JWK to cache.
	 * @param ttl the time-to-live in seconds for the cached JWK.
	 */
	void cacheJwk(String url, CachedJwk jwk, long ttl);

	/**
	 * Caches an indication that there is no JWK associated with the provided Key ID (kid) at the
	 * specified URL.
	 *
	 * @param url the URL associated with the JWK.
	 * @param kid the Key ID that is absent.
	 * @param ttl the time-to-live in seconds for this absence record.
	 */
	void cacheJwkAbsent(String url, String kid, long ttl);

	/**
	 * Retrieves a list of cached JWKs associated with the specified URL.
	 *
	 * @param url the URL to retrieve the JWKs from.
	 * @return a list of CachedJWK objects or an empty list if none are found.
	 */
	List<CachedJwk> getJwks(String url);

	/**
	 * Retrieves a single cached JWK associated with the specified URL and Key ID (kid).
	 * 
	 * If no value
	 *
	 * @param url the URL to retrieve the JWK from.
	 * @param kid the Key ID of the JWK to retrieve.
	 * @return the CachedJwk if found, or null if no matching JWK is cached.
	 */
	CachedJwk getJwk(String url, String kid);
}
