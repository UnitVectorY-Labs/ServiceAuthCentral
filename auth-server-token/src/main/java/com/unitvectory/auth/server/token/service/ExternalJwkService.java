package com.unitvectory.auth.server.token.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.unitvectory.auth.common.service.time.TimeService;
import com.unitvectory.auth.datamodel.model.CachedJwk;
import com.unitvectory.auth.datamodel.repository.JwkCacheRepository;
import com.unitvectory.auth.server.token.mapper.JwkMapper;
import com.unitvectory.auth.verify.model.VerifyJwk;
import com.unitvectory.auth.verify.model.VerifyJwks;
import com.unitvectory.auth.verify.service.JwksResolver;

import lombok.NonNull;

@Service
public class ExternalJwkService {

	@Value("${serviceauthcentral.server.token.external.cache.seconds:3600}")
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
				this.jwkCacheRepository.cacheJwk(url, JwkMapper.INSTANCE.verifyJwkToCachedJwk(jwk), now);

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
