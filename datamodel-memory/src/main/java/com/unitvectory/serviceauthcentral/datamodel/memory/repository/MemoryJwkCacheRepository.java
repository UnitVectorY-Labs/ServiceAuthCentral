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
package com.unitvectory.serviceauthcentral.datamodel.memory.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.unitvectory.serviceauthcentral.datamodel.memory.mapper.MemoryCachedJwkMapper;
import com.unitvectory.serviceauthcentral.datamodel.memory.model.MemoryCachedJwk;
import com.unitvectory.serviceauthcentral.datamodel.model.CachedJwk;
import com.unitvectory.serviceauthcentral.datamodel.repository.JwkCacheRepository;

import lombok.NonNull;

/**
 * The Memory JWK Cache Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
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
		MemoryCachedJwk cachedJwk =
				MemoryCachedJwkMapper.INSTANCE.cachedJwkToMemoryCachedJwk(url, ttl, jwk);
		cache.put(key, cachedJwk);
	}

	@Override
	public void cacheJwkAbsent(@NonNull String url, @NonNull String kid, long ttl) {
		String key = key(url, kid);

		MemoryCachedJwk cachedJwk =
				MemoryCachedJwk.builder().url(url).kid(kid).ttl(ttl).valid(false).build();
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
