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
package com.unitvectory.serviceauthcentral.datamodel.valkey.repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;

import com.unitvectory.serviceauthcentral.datamodel.model.CachedJwk;
import com.unitvectory.serviceauthcentral.datamodel.repository.JwkCacheRepository;
import com.unitvectory.serviceauthcentral.datamodel.valkey.mapper.ValkeyCachedJwkMapper;
import com.unitvectory.serviceauthcentral.datamodel.valkey.model.ValkeyCachedJwk;

import lombok.NonNull;

/**
 * The Valkey JWK Cache Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public class ValkeyJwkCacheRepository implements JwkCacheRepository {

	private static final String JWK_KEY_PREFIX = "sac:jwk:";
	private static final String JWK_URL_INDEX_PREFIX = "sac:jwk:url:";

	private final StringRedisTemplate redisTemplate;

	public ValkeyJwkCacheRepository(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	private String jwkKey(String url, String kid) {
		return JWK_KEY_PREFIX + url + ":" + kid;
	}

	private String urlIndexKey(String url) {
		return JWK_URL_INDEX_PREFIX + url;
	}

	@Override
	public void cacheJwk(@NonNull String url, @NonNull CachedJwk jwk, long ttl) {
		ValkeyCachedJwk cachedJwk =
				ValkeyCachedJwkMapper.INSTANCE.cachedJwkToValkeyCachedJwk(url, ttl, jwk);

		String key = jwkKey(url, jwk.getKid());

		saveJwkToHash(key, cachedJwk);
		redisTemplate.expireAt(key, Instant.ofEpochSecond(ttl));

		// Add kid to URL index
		redisTemplate.opsForSet().add(urlIndexKey(url), jwk.getKid());
	}

	@Override
	public void cacheJwkAbsent(@NonNull String url, @NonNull String kid, long ttl) {
		ValkeyCachedJwk cachedJwk =
				ValkeyCachedJwk.builder().url(url).kid(kid).ttl(ttl).valid(false).build();

		String key = jwkKey(url, kid);

		saveJwkToHash(key, cachedJwk);
		redisTemplate.expireAt(key, Instant.ofEpochSecond(ttl));

		// Add kid to URL index
		redisTemplate.opsForSet().add(urlIndexKey(url), kid);
	}

	@Override
	public List<CachedJwk> getJwks(@NonNull String url) {
		Set<String> kids = redisTemplate.opsForSet().members(urlIndexKey(url));
		List<CachedJwk> list = new ArrayList<>();
		if (kids != null) {
			for (String kid : kids) {
				CachedJwk jwk = getJwk(url, kid);
				if (jwk != null) {
					list.add(jwk);
				}
			}
		}
		return Collections.unmodifiableList(list);
	}

	@Override
	public CachedJwk getJwk(@NonNull String url, @NonNull String kid) {
		String key = jwkKey(url, kid);
		Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
		if (entries == null || entries.isEmpty()) {
			return null;
		}
		return hashToJwk(entries);
	}

	private void saveJwkToHash(String key, ValkeyCachedJwk jwk) {
		redisTemplate.delete(key);

		redisTemplate.opsForHash().put(key, "url", nullSafe(jwk.getUrl()));
		redisTemplate.opsForHash().put(key, "ttl", String.valueOf(jwk.getTtl()));
		redisTemplate.opsForHash().put(key, "valid", String.valueOf(jwk.isValid()));
		redisTemplate.opsForHash().put(key, "kid", nullSafe(jwk.getKid()));
		redisTemplate.opsForHash().put(key, "kty", nullSafe(jwk.getKty()));
		redisTemplate.opsForHash().put(key, "alg", nullSafe(jwk.getAlg()));
		redisTemplate.opsForHash().put(key, "use", nullSafe(jwk.getUse()));
		redisTemplate.opsForHash().put(key, "n", nullSafe(jwk.getN()));
		redisTemplate.opsForHash().put(key, "e", nullSafe(jwk.getE()));
	}

	private ValkeyCachedJwk hashToJwk(Map<Object, Object> entries) {
		return ValkeyCachedJwk.builder()
				.url(getStr(entries, "url"))
				.ttl(getLong(entries, "ttl"))
				.valid(Boolean.parseBoolean(getStrOrDefault(entries, "valid", "false")))
				.kid(getStr(entries, "kid"))
				.kty(getStr(entries, "kty"))
				.alg(getStr(entries, "alg"))
				.use(getStr(entries, "use"))
				.n(getStr(entries, "n"))
				.e(getStr(entries, "e"))
				.build();
	}

	private String getStr(Map<Object, Object> entries, String field) {
		Object val = entries.get(field);
		if (val == null) {
			return null;
		}
		String str = val.toString();
		return str.isEmpty() ? null : str;
	}

	private String getStrOrDefault(Map<Object, Object> entries, String field,
			String defaultValue) {
		Object val = entries.get(field);
		if (val == null) {
			return defaultValue;
		}
		String str = val.toString();
		return str.isEmpty() ? defaultValue : str;
	}

	private long getLong(Map<Object, Object> entries, String field) {
		Object val = entries.get(field);
		if (val == null) {
			return 0;
		}
		return Long.parseLong(val.toString());
	}

	private String nullSafe(String value) {
		return value != null ? value : "";
	}
}
