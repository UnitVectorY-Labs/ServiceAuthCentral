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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unitvectory.consistgen.epoch.EpochTimeProvider;
import com.unitvectory.serviceauthcentral.datamodel.model.Authorization;
import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.time.TimeUtil;
import com.unitvectory.serviceauthcentral.datamodel.valkey.model.ValkeyAuthorization;
import com.unitvectory.serviceauthcentral.util.HashingUtil;
import com.unitvectory.serviceauthcentral.util.exception.InternalServerErrorException;

import lombok.NonNull;

/**
 * The Valkey Authorization Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public class ValkeyAuthorizationRepository implements AuthorizationRepository {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private static final String AUTH_KEY_PREFIX = "sac:auth:";
	private static final String AUTH_SUBJECT_INDEX_PREFIX = "sac:auth:subject:";
	private static final String AUTH_AUDIENCE_INDEX_PREFIX = "sac:auth:audience:";
	private static final String AUTH_LOOKUP_PREFIX = "sac:auth:lookup:";

	private final StringRedisTemplate redisTemplate;
	private final EpochTimeProvider epochTimeProvider;

	public ValkeyAuthorizationRepository(StringRedisTemplate redisTemplate,
			EpochTimeProvider epochTimeProvider) {
		this.redisTemplate = redisTemplate;
		this.epochTimeProvider = epochTimeProvider;
	}

	private String authKey(String documentId) {
		return AUTH_KEY_PREFIX + documentId;
	}

	private String subjectIndexKey(String subject) {
		return AUTH_SUBJECT_INDEX_PREFIX + subject;
	}

	private String audienceIndexKey(String audience) {
		return AUTH_AUDIENCE_INDEX_PREFIX + audience;
	}

	private String lookupKey(String subject, String audience) {
		String subjectHash = HashingUtil.sha256(subject);
		String audienceHash = HashingUtil.sha256(audience);
		return AUTH_LOOKUP_PREFIX + subjectHash + ":" + audienceHash;
	}

	@Override
	public Authorization getAuthorization(@NonNull String id) {
		String key = authKey(id);
		Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
		if (entries == null || entries.isEmpty()) {
			return null;
		}
		return hashToAuthorization(entries);
	}

	@Override
	public void deleteAuthorization(@NonNull String id) {
		Authorization auth = getAuthorization(id);
		if (auth != null) {
			// Clean up indexes
			redisTemplate.opsForSet().remove(subjectIndexKey(auth.getSubject()), id);
			redisTemplate.opsForSet().remove(audienceIndexKey(auth.getAudience()), id);
			redisTemplate.delete(lookupKey(auth.getSubject(), auth.getAudience()));
			redisTemplate.delete(authKey(id));
		}
	}

	@Override
	public Authorization getAuthorization(@NonNull String subject, @NonNull String audience) {
		String lookup = lookupKey(subject, audience);
		String documentId = redisTemplate.opsForValue().get(lookup);
		if (documentId == null) {
			return null;
		}
		return getAuthorization(documentId);
	}

	@Override
	public Iterator<Authorization> getAuthorizationBySubject(@NonNull String subject) {
		Set<String> documentIds = redisTemplate.opsForSet().members(subjectIndexKey(subject));
		List<Authorization> list = new ArrayList<>();
		if (documentIds != null) {
			for (String docId : documentIds) {
				Authorization auth = getAuthorization(docId);
				if (auth != null) {
					list.add(auth);
				}
			}
		}
		return list.iterator();
	}

	@Override
	public Iterator<Authorization> getAuthorizationByAudience(@NonNull String audience) {
		Set<String> documentIds = redisTemplate.opsForSet().members(audienceIndexKey(audience));
		List<Authorization> list = new ArrayList<>();
		if (documentIds != null) {
			for (String docId : documentIds) {
				Authorization auth = getAuthorization(docId);
				if (auth != null) {
					list.add(auth);
				}
			}
		}
		return list.iterator();
	}

	@Override
	public void authorize(@NonNull String subject, @NonNull String audience,
			@NonNull List<String> authorizedScopes) {

		String lookup = lookupKey(subject, audience);
		// Check if already exists
		String existingDocId = redisTemplate.opsForValue().get(lookup);
		if (existingDocId != null) {
			return;
		}

		String now = TimeUtil.getCurrentTimestamp(this.epochTimeProvider.epochTimeSeconds());

		ValkeyAuthorization auth = ValkeyAuthorization.builder().authorizationCreated(now)
				.subject(subject).audience(audience)
				.authorizedScopes(new ArrayList<>(authorizedScopes)).build();

		String documentId = auth.getDocumentId();
		String key = authKey(documentId);

		saveAuthorizationToHash(key, auth);

		// Add to indexes
		redisTemplate.opsForValue().set(lookup, documentId);
		redisTemplate.opsForSet().add(subjectIndexKey(subject), documentId);
		redisTemplate.opsForSet().add(audienceIndexKey(audience), documentId);
	}

	@Override
	public void deauthorize(@NonNull String subject, @NonNull String audience) {
		String lookup = lookupKey(subject, audience);
		String documentId = redisTemplate.opsForValue().get(lookup);
		if (documentId != null) {
			redisTemplate.delete(authKey(documentId));
			redisTemplate.delete(lookup);
			redisTemplate.opsForSet().remove(subjectIndexKey(subject), documentId);
			redisTemplate.opsForSet().remove(audienceIndexKey(audience), documentId);
		}
	}

	@Override
	public void authorizeAddScope(@NonNull String subject, @NonNull String audience,
			@NonNull String authorizedScope) {
		String lookup = lookupKey(subject, audience);
		String documentId = redisTemplate.opsForValue().get(lookup);
		if (documentId == null) {
			throw new InternalServerErrorException("Authorization not found");
		}

		String key = authKey(documentId);
		Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
		if (entries == null || entries.isEmpty()) {
			throw new InternalServerErrorException("Authorization not found");
		}

		ValkeyAuthorization auth = hashToAuthorization(entries);
		List<String> scopes = new ArrayList<>(auth.getAuthorizedScopes());
		scopes.add(authorizedScope);

		auth = ValkeyAuthorization.builder().authorizationCreated(auth.getAuthorizationCreated())
				.subject(auth.getSubject()).audience(auth.getAudience())
				.authorizedScopes(scopes).build();

		saveAuthorizationToHash(key, auth);
	}

	@Override
	public void authorizeRemoveScope(@NonNull String subject, @NonNull String audience,
			@NonNull String authorizedScope) {
		String lookup = lookupKey(subject, audience);
		String documentId = redisTemplate.opsForValue().get(lookup);
		if (documentId == null) {
			throw new InternalServerErrorException("Authorization not found");
		}

		String key = authKey(documentId);
		Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
		if (entries == null || entries.isEmpty()) {
			throw new InternalServerErrorException("Authorization not found");
		}

		ValkeyAuthorization auth = hashToAuthorization(entries);
		List<String> scopes = new ArrayList<>(auth.getAuthorizedScopes());
		scopes.remove(authorizedScope);

		auth = ValkeyAuthorization.builder().authorizationCreated(auth.getAuthorizationCreated())
				.subject(auth.getSubject()).audience(auth.getAudience())
				.authorizedScopes(scopes).build();

		saveAuthorizationToHash(key, auth);
	}

	private void saveAuthorizationToHash(String key, ValkeyAuthorization auth) {
		redisTemplate.delete(key);

		redisTemplate.opsForHash().put(key, "authorizationCreated",
				nullSafe(auth.getAuthorizationCreated()));
		redisTemplate.opsForHash().put(key, "subject", nullSafe(auth.getSubject()));
		redisTemplate.opsForHash().put(key, "audience", nullSafe(auth.getAudience()));

		try {
			String scopesJson = OBJECT_MAPPER.writeValueAsString(auth.getAuthorizedScopes());
			redisTemplate.opsForHash().put(key, "authorizedScopes", scopesJson);
		} catch (JsonProcessingException e) {
			throw new InternalServerErrorException("Failed to serialize authorization scopes");
		}
	}

	private ValkeyAuthorization hashToAuthorization(Map<Object, Object> entries) {
		String authorizationCreated = getStr(entries, "authorizationCreated");
		String subject = getStr(entries, "subject");
		String audience = getStr(entries, "audience");

		List<String> authorizedScopes = new ArrayList<>();
		String scopesJson = getStr(entries, "authorizedScopes");
		if (scopesJson != null && !scopesJson.isEmpty()) {
			try {
				authorizedScopes = OBJECT_MAPPER.readValue(scopesJson,
						new TypeReference<List<String>>() {
						});
			} catch (JsonProcessingException e) {
				throw new InternalServerErrorException(
						"Failed to deserialize authorization scopes");
			}
		}

		return ValkeyAuthorization.builder().authorizationCreated(authorizationCreated)
				.subject(subject).audience(audience).authorizedScopes(authorizedScopes).build();
	}

	private String getStr(Map<Object, Object> entries, String field) {
		Object val = entries.get(field);
		if (val == null) {
			return null;
		}
		String str = val.toString();
		return str.isEmpty() ? null : str;
	}

	private String nullSafe(String value) {
		return value != null ? value : "";
	}
}
