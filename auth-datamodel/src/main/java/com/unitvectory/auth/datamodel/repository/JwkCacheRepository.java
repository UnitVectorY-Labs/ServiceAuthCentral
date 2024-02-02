/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.unitvectory.auth.datamodel.repository;

import java.util.List;

import com.unitvectory.auth.datamodel.model.CachedJwk;

/**
 * Interface for repository handling the caching of JSON Web Keys (JWK).
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
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
