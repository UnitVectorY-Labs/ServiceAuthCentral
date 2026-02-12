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

import static com.unitvectory.serviceauthcentral.datamodel.valkey.repository.ValkeyHashUtil.*;

import java.time.Instant;
import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;

import com.unitvectory.serviceauthcentral.datamodel.model.LoginCode;
import com.unitvectory.serviceauthcentral.datamodel.repository.LoginCodeRepository;
import com.unitvectory.serviceauthcentral.datamodel.valkey.model.ValkeyLoginCode;

/**
 * The Valkey Login Code Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public class ValkeyLoginCodeRepository implements LoginCodeRepository {

	private static final String LOGIN_CODE_KEY_PREFIX = "sac:logincode:";

	private final StringRedisTemplate redisTemplate;

	public ValkeyLoginCodeRepository(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	private String codeKey(String code) {
		return LOGIN_CODE_KEY_PREFIX + code;
	}

	@Override
	public void saveCode(String code, String clientId, String redirectUri, String codeChallenge,
			String userClientId, long ttl) {
		String key = codeKey(code);

		redisTemplate.opsForHash().put(key, "clientId", nullSafe(clientId));
		redisTemplate.opsForHash().put(key, "redirectUri", nullSafe(redirectUri));
		redisTemplate.opsForHash().put(key, "codeChallenge", nullSafe(codeChallenge));
		redisTemplate.opsForHash().put(key, "userClientId", nullSafe(userClientId));
		redisTemplate.opsForHash().put(key, "ttl", String.valueOf(ttl));

		redisTemplate.expireAt(key, Instant.ofEpochSecond(ttl));
	}

	@Override
	public LoginCode getCode(String code) {
		String key = codeKey(code);
		Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
		if (entries == null || entries.isEmpty()) {
			return null;
		}
		return hashToLoginCode(entries);
	}

	@Override
	public void deleteCode(String code) {
		redisTemplate.delete(codeKey(code));
	}

	private ValkeyLoginCode hashToLoginCode(Map<Object, Object> entries) {
		return ValkeyLoginCode.builder()
				.clientId(getStr(entries, "clientId"))
				.redirectUri(getStr(entries, "redirectUri"))
				.codeChallenge(getStr(entries, "codeChallenge"))
				.userClientId(getStr(entries, "userClientId"))
				.ttl(getLong(entries, "ttl"))
				.build();
	}

}
