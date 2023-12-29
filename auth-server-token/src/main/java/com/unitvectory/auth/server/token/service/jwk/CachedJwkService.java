package com.unitvectory.auth.server.token.service.jwk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

import com.auth0.jwk.Jwk;
import com.unitvectory.auth.datamodel.exception.InternalServerErrorException;
import com.unitvectory.auth.datamodel.model.CachedJwk;
import com.unitvectory.auth.datamodel.repository.JwkCacheRepository;
import com.unitvectory.auth.server.token.config.AppConfig;
import com.unitvectory.auth.server.token.model.PulledJwk;
import com.unitvectory.auth.server.token.service.time.TimeService;

public class CachedJwkService implements JwksService {

	private JwksService jwksService;

	@Autowired
	private JwkCacheRepository jwkCacheRepository;

	@Autowired
	private TimeService timeService;

	@Autowired
	private AppConfig appConfig;

	public CachedJwkService(JwksService jwksService) {
		this.jwksService = jwksService;
	}

	private Jwk convert(CachedJwk cachedJwk) {

		// TODO: Use a real mapper
		Map<String, Object> map = new HashMap<>();
		map.put("kid", cachedJwk.getKid());
		map.put("kty", cachedJwk.getKty());
		map.put("alg", cachedJwk.getAlg());
		map.put("use", cachedJwk.getUse());
		map.put("n", cachedJwk.getN());
		map.put("e", cachedJwk.getE());

		return Jwk.fromValues(map);
	}

	private CachedJwk convert(Jwk jwk) {

		// TODO: Use a real mapper
		return new PulledJwk(jwk);
	}

	@Override
	@Cacheable("keySetLookupCache")
	public Jwk getJwk(String url, String kid) {

		// Current time needed to determine how long to utilize cache for before calling
		// the endpoint again
		long now = timeService.getCurrentTimeSeconds();

		// Look up in repository first before reaching out to endpoint
		CachedJwk cachedJwk;
		try {
			cachedJwk = this.jwkCacheRepository.getJwk(url, kid);
		} catch (Exception e) {
			throw new InternalServerErrorException("failed to get key from cache", e);
		}
		Jwk foundJwk = null;

		boolean refresh = false;
		if (cachedJwk == null) {
			// No hit, refresh
			refresh = true;
		} else if (cachedJwk.isExpired(now)) {
			// Hit but expired, refresh
			refresh = true;
		} else {
			// Use the cached version
			foundJwk = convert(cachedJwk);
		}

		// Refresh needed, look it up
		if (refresh) {
			List<Jwk> jwks;
			try {
				// Make the remote call to get all of the JWKS
				jwks = this.jwksService.getJwks(url);
			} catch (Exception e) {
				// This is bad, there may be some kind of issue, so the goal is to
				if (cachedJwk != null) {
					// The cached version saved the failure here
					return convert(cachedJwk);
				}

				// This is pretty bad, but nothing else we can do
				throw e;
			}

			boolean found = false;
			for (Jwk jwk : jwks) {

				// Cache the value in the database
				try {
					this.jwkCacheRepository.cacheJwk(url, convert(jwk),
							now + (this.appConfig.getCacheJwksHours() * 60 * 60));
				} catch (Exception e) {
					throw new InternalServerErrorException("failed to save jwk to cache", e);
				}

				// Check to see if we found the key we were looking for
				if (kid.equals(jwk.getId())) {
					// It was, we found it so that is what we will return
					found = true;
					foundJwk = jwk;
				}
			}

			if (!found) {
				// Key wasn't found, mark it in the database as not found and we aren't
				// returning it easier as it may have been removed
				try {
					this.jwkCacheRepository.cacheJwkAbsent(url, kid,
							now + (this.appConfig.getCacheJwksHours() * 60 * 60));
				} catch (Exception e) {
					throw new InternalServerErrorException("failed to save jwk to cache", e);
				}
				foundJwk = null;
			}
		}

		return foundJwk;
	}

	@Override
	public List<Jwk> getJwks(String url) {

		// Current time needed to determine how long to utilize cache for before calling
		// the endpoint again
		long now = timeService.getCurrentTimeSeconds();

		List<CachedJwk> jwks;
		try {
			jwks = this.jwkCacheRepository.getJwks(url);
		} catch (Exception e) {
			throw new InternalServerErrorException("failed to get key from cache", e);
		}

		List<Jwk> list = new ArrayList<>();

		// If none of the items are expired, just return the expired
		boolean expired = false;
		for (CachedJwk cachedJwk : jwks) {
			if (!cachedJwk.isValid()) {
				continue;
			}

			if (cachedJwk.isExpired(now)) {
				expired = true;
				break;
			}

			list.add(convert(cachedJwk));

		}

		if (!expired) {
			return Collections.unmodifiableList(list);
		}

		list = this.jwksService.getJwks(url);

		// Cache everything
		for (Jwk jwk : list) {
			// Cache the value in the database
			try {
				this.jwkCacheRepository.cacheJwk(url, convert(jwk),
						now + (this.appConfig.getCacheJwksHours() * 60 * 60));
			} catch (Exception e) {
				throw new InternalServerErrorException("failed to save jwk to cache", e);
			}
		}

		return Collections.unmodifiableList(list);
	}
}
