package com.unitvectory.serviceauthcentral.service;

import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.UrlJwkProvider;
import com.unitvectory.serviceauthcentral.dto.CachedJwk;
import com.unitvectory.serviceauthcentral.repository.KeySetRepository;

@Service
public class KeySetLookupService {

	@Autowired
	private KeySetRepository keySetRepository;

	@Autowired
	private TimeService timeService;

	@Value("${serviceauthcentral.cache.jwks.hours}")
	private int cacheJwksHours;

	@Cacheable("keySetLookupCache")
	public Jwk getJwk(String url, String kid) throws Exception {

		// Current time needed to determine how long to utilize cache for before calling
		// the endpoint again
		long now = timeService.getCurrentTimeSeconds();

		// Look up in repository first before reaching out to endpoint
		CachedJwk cachedJwk = this.keySetRepository.getKey(url, kid);
		Jwk foundJwk = null;

		boolean refresh = false;
		if (cachedJwk == null) {
			// No hit, refresh
			refresh = true;
		} else if (cachedJwk.getCached().getEpochSecond() < (now - (this.cacheJwksHours * 60 * 60))) {
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
				// This makes the actual HTTP call out to get the keys
				UrlJwkProvider provider = new UrlJwkProvider(new URL(url));

				// No reason not to get all of the JWKs and avoid potential future misses, these
				// all can be loaded into the database without causing any harm
				jwks = provider.getAll();
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
				this.keySetRepository.saveKey(url, jwk);

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
				this.keySetRepository.saveNoKey(url, kid);
				foundJwk = null;
			}
		}

		return foundJwk;
	}
}
