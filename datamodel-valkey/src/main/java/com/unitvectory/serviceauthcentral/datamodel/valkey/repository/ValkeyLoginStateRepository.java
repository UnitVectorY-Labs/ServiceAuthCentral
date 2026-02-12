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
import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;

import com.unitvectory.serviceauthcentral.datamodel.model.LoginState;
import com.unitvectory.serviceauthcentral.datamodel.repository.LoginStateRepository;
import com.unitvectory.serviceauthcentral.datamodel.valkey.model.ValkeyLoginState;

/**
 * The Valkey Login State Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public class ValkeyLoginStateRepository implements LoginStateRepository {

	private static final String LOGIN_STATE_KEY_PREFIX = "sac:loginstate:";

	private final StringRedisTemplate redisTemplate;

	public ValkeyLoginStateRepository(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	private String stateKey(String sessionId) {
		return LOGIN_STATE_KEY_PREFIX + sessionId;
	}

	@Override
	public void saveState(String sessionId, String clientId, String redirectUri,
			String primaryState, String primaryCodeChallenge, String secondaryState, long ttl) {
		String key = stateKey(sessionId);

		redisTemplate.opsForHash().put(key, "clientId", nullSafe(clientId));
		redisTemplate.opsForHash().put(key, "redirectUri", nullSafe(redirectUri));
		redisTemplate.opsForHash().put(key, "primaryState", nullSafe(primaryState));
		redisTemplate.opsForHash().put(key, "primaryCodeChallenge",
				nullSafe(primaryCodeChallenge));
		redisTemplate.opsForHash().put(key, "secondaryState", nullSafe(secondaryState));
		redisTemplate.opsForHash().put(key, "ttl", String.valueOf(ttl));

		redisTemplate.expireAt(key, Instant.ofEpochSecond(ttl));
	}

	@Override
	public LoginState getState(String sessionId) {
		String key = stateKey(sessionId);
		Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
		if (entries == null || entries.isEmpty()) {
			return null;
		}
		return hashToLoginState(entries);
	}

	@Override
	public void deleteState(String sessionId) {
		redisTemplate.delete(stateKey(sessionId));
	}

	private ValkeyLoginState hashToLoginState(Map<Object, Object> entries) {
		return ValkeyLoginState.builder()
				.clientId(getStr(entries, "clientId"))
				.redirectUri(getStr(entries, "redirectUri"))
				.primaryState(getStr(entries, "primaryState"))
				.primaryCodeChallenge(getStr(entries, "primaryCodeChallenge"))
				.secondaryState(getStr(entries, "secondaryState"))
				.ttl(getLong(entries, "ttl"))
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
