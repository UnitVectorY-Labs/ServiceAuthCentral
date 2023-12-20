package com.unitvectory.serviceauthcentral.service.jwk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

import com.auth0.jwk.Jwk;
import com.unitvectory.serviceauthcentral.config.AppConfig;
import com.unitvectory.serviceauthcentral.dto.CachedJwk;
import com.unitvectory.serviceauthcentral.exception.InternalServerErrorException;
import com.unitvectory.serviceauthcentral.repository.key.KeySetRepository;
import com.unitvectory.serviceauthcentral.service.time.TimeService;

public class CachedJwkService implements JwksService {

	private JwksService jwksService;

	@Autowired
	private KeySetRepository keySetRepository;

	@Autowired
	private TimeService timeService;

	@Autowired
	private AppConfig appConfig;

	public CachedJwkService(JwksService jwksService) {
		this.jwksService = jwksService;
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
			cachedJwk = this.keySetRepository.getKey(url, kid);
		} catch (Exception e) {
			throw new InternalServerErrorException("failed to get key from cache", e);
		}
		Jwk foundJwk = null;

		boolean refresh = false;
		if (cachedJwk == null) {
			// No hit, refresh
			refresh = true;
		} else if (cachedJwk.isExpiredAfterHours(now, this.appConfig.getCacheJwksHours())) {
			// Hit but expired, refresh
			refresh = true;
		} else {
			// Use the cached version
			foundJwk = cachedJwk.getJwk();
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
					return cachedJwk.getJwk();
				}

				// This is pretty bad, but nothing else we can do
				throw e;
			}

			boolean found = false;
			for (Jwk jwk : jwks) {

				// Cache the value in the database
				try {
					this.keySetRepository.saveKey(url, jwk);
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
					this.keySetRepository.saveNoKey(url, kid);
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
			jwks = this.keySetRepository.getKeys(url);
		} catch (Exception e) {
			throw new InternalServerErrorException("failed to get key from cache", e);
		}

		List<Jwk> list = new ArrayList<>();

		// If none of the items are expired, just return the expired
		boolean expired = false;
		for (CachedJwk cachedJwk : jwks) {
			if (cachedJwk.getJwk() == null) {
				continue;
			}

			if (cachedJwk.isExpiredAfterHours(now, this.appConfig.getCacheJwksHours())) {
				expired = true;
				break;
			}

			list.add(cachedJwk.getJwk());

		}

		if (!expired) {
			return Collections.unmodifiableList(list);
		}

		list = this.jwksService.getJwks(url);

		// Cache everything
		for (Jwk jwk : list) {
			// Cache the value in the database
			try {
				this.keySetRepository.saveKey(url, jwk);
			} catch (Exception e) {
				throw new InternalServerErrorException("failed to save jwk to cache", e);
			}
		}

		return Collections.unmodifiableList(list);
	}
}
