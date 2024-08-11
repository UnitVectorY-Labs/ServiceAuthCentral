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
package com.unitvectory.serviceauthcentral.server.token.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.unitvectory.serviceauthcentral.common.service.time.TimeService;
import com.unitvectory.serviceauthcentral.datamodel.model.CachedJwk;
import com.unitvectory.serviceauthcentral.datamodel.repository.JwkCacheRepository;
import com.unitvectory.serviceauthcentral.server.token.mapper.JwkMapper;
import com.unitvectory.serviceauthcentral.verify.model.VerifyJwk;
import com.unitvectory.serviceauthcentral.verify.model.VerifyJwks;
import com.unitvectory.serviceauthcentral.verify.service.JwksResolver;

import lombok.NonNull;

/**
 * The External JWK Service
 * 
 * Used to retrieve remote JWK records so they can be used to validate
 * urn:ietf:params:oauth:grant-type:jwt-bearer requests
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Service
public class ExternalJwkService {

	@Value("${sac.server.token.external.cache.seconds:3600}")
	private long externalCacheHours;

	@Autowired
	private JwksResolver jwksResolver;

	@Autowired
	private JwkCacheRepository jwkCacheRepository;

	@Autowired
	private TimeService timeService;

	/**
	 * Gets the JWK used to verify a JWT
	 * 
	 * Will utilize caches to improve performance and availability.
	 * 
	 * @param url the JWKS url
	 * @param kid the kid
	 * @return the jwk details needed for verification
	 */
	public VerifyJwk getJwk(@NonNull String url, @NonNull String kid) {

		long now = timeService.getCurrentTimeSeconds();

		// First, try to get the key from the database cache
		CachedJwk cachedJwk = this.jwkCacheRepository.getJwk(url, kid);

		if (cachedJwk == null || cachedJwk.isExpired(now)) {
			// Not found or expired, need to look it up externally

			// Get the JWKs from the remote server
			VerifyJwks jwks = this.jwksResolver.getJwks(url);

			// We are going to look for a match
			VerifyJwk matchedJwk = null;

			// Cache all of the keys in the database again
			for (VerifyJwk jwk : jwks.getKeys()) {
				this.jwkCacheRepository.cacheJwk(url,
						JwkMapper.INSTANCE.verifyJwkToCachedJwk(url, jwk), now);

				// Look for the matching JWK based on KID
				if (kid.equals(jwk.getKid())) {
					matchedJwk = jwk;
				}
			}

			// Match not found, so we will cache that
			if (matchedJwk == null) {
				this.jwkCacheRepository.cacheJwkAbsent(url, kid, externalCacheHours);
			}

			return matchedJwk;

		} else if (!cachedJwk.isValid()) {
			// Cache of a missing JWK, return that it wasn't found
			return null;
		} else {
			// Cache hit, return it
			return JwkMapper.INSTANCE.cachedJwkToVerifyJwk(cachedJwk);
		}
	}
}
