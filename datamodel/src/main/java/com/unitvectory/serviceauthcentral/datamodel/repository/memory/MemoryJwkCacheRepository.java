package com.unitvectory.serviceauthcentral.datamodel.repository.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.unitvectory.serviceauthcentral.datamodel.model.CachedJwk;
import com.unitvectory.serviceauthcentral.datamodel.repository.JwkCacheRepository;

import lombok.NonNull;

public class MemoryJwkCacheRepository implements JwkCacheRepository {

	private Map<String, MemoryCachedJwk> cache;

	public MemoryJwkCacheRepository() {
		this.cache = new HashMap<>();
	}

	public void reset() {
		this.cache.clear();
	}

	private String key(@NonNull String url, @NonNull String kid) {
		return url + "||" + kid;
	}

	@Override
	public void cacheJwk(@NonNull String url, @NonNull CachedJwk jwk, long ttl) {
		String key = key(url, jwk.getKid());

		// Make a copy of the item
		MemoryCachedJwk cachedJwk = MemoryCachedJwkMapper.INSTANCE.cachedJwkToMemoryCachedJwk(url, ttl, jwk);
		cache.put(key, cachedJwk);
	}

	@Override
	public void cacheJwkAbsent(@NonNull String url, @NonNull String kid, long ttl) {
		String key = key(url, kid);

		MemoryCachedJwk cachedJwk = MemoryCachedJwk.builder().url(url).kid(kid).ttl(ttl).valid(false).build();
		cache.put(key, cachedJwk);
	}

	@Override
	public List<CachedJwk> getJwks(@NonNull String url) {
		List<CachedJwk> list = new ArrayList<>();

		// Note: This isn't paying attention to TTL

		for (MemoryCachedJwk jwk : this.cache.values()) {
			if (url.equals(jwk.getUrl())) {
				list.add(jwk);
			}
		}

		return Collections.unmodifiableList(list);
	}

	@Override
	public CachedJwk getJwk(@NonNull String url, @NonNull String kid) {
		String key = key(url, kid);
		MemoryCachedJwk jwk = this.cache.get(key);

		// Note: This isn't paying attention to TTL

		return jwk;
	}

}
