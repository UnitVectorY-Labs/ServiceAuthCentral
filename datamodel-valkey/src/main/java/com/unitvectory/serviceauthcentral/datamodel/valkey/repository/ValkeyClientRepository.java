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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unitvectory.consistgen.epoch.EpochTimeProvider;
import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientJwtBearer;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientScope;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientSummary;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientSummaryConnection;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientSummaryEdge;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientType;
import com.unitvectory.serviceauthcentral.datamodel.model.PageInfo;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.datamodel.time.TimeUtil;
import com.unitvectory.serviceauthcentral.datamodel.valkey.mapper.ClientScopeMapper;
import com.unitvectory.serviceauthcentral.datamodel.valkey.mapper.ValkeyClientSummaryMapper;
import com.unitvectory.serviceauthcentral.datamodel.valkey.model.ValkeyClient;
import com.unitvectory.serviceauthcentral.datamodel.valkey.model.ValkeyClientJwtBearer;
import com.unitvectory.serviceauthcentral.datamodel.valkey.model.ValkeyClientScope;
import com.unitvectory.serviceauthcentral.util.exception.BadRequestException;
import com.unitvectory.serviceauthcentral.util.exception.InternalServerErrorException;
import com.unitvectory.serviceauthcentral.util.exception.NotFoundException;

import lombok.NonNull;

/**
 * The Valkey Client Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public class ValkeyClientRepository implements ClientRepository {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private static final String CLIENT_KEY_PREFIX = "sac:client:";
	private static final String CLIENTS_INDEX_KEY = "sac:clients";

	private final StringRedisTemplate redisTemplate;
	private final EpochTimeProvider epochTimeProvider;

	public ValkeyClientRepository(StringRedisTemplate redisTemplate,
			EpochTimeProvider epochTimeProvider) {
		this.redisTemplate = redisTemplate;
		this.epochTimeProvider = epochTimeProvider;
	}

	private String clientKey(String clientId) {
		return CLIENT_KEY_PREFIX + clientId;
	}

	@Override
	public ClientSummaryConnection getClients(Integer first, String after, Integer last,
			String before) {
		List<ClientSummaryEdge> edges = new ArrayList<>();
		boolean hasNextPage = false;
		boolean hasPreviousPage = false;

		Long totalSize = redisTemplate.opsForZSet().zCard(CLIENTS_INDEX_KEY);
		if (totalSize == null) {
			totalSize = 0L;
		}
		int total = totalSize.intValue();

		Integer afterIndex = after != null
				? Integer.parseInt(
						new String(Base64.getDecoder().decode(after), StandardCharsets.UTF_8))
				: null;
		Integer beforeIndex = before != null
				? Integer.parseInt(
						new String(Base64.getDecoder().decode(before), StandardCharsets.UTF_8))
				: null;

		int startIndex = 0;
		int endIndex = total;
		if (first != null && afterIndex != null) {
			startIndex = afterIndex;
			endIndex = Math.min(startIndex + first, total);
			hasNextPage = endIndex < total;
			hasPreviousPage = startIndex > 0;
		} else if (last != null && beforeIndex != null) {
			endIndex = beforeIndex;
			startIndex = Math.max(endIndex - last, 0);
			hasNextPage = endIndex < total;
			hasPreviousPage = startIndex > 0;
		} else if (first != null) {
			endIndex = Math.min(first, total);
			hasNextPage = endIndex < total;
		} else if (last != null) {
			startIndex = Math.max(total - last, 0);
			hasPreviousPage = startIndex > 0;
		}

		if (startIndex < endIndex) {
			Set<String> clientIds =
					redisTemplate.opsForZSet().range(CLIENTS_INDEX_KEY, startIndex, endIndex - 1);
			if (clientIds != null) {
				int index = startIndex;
				for (String clientId : clientIds) {
					ValkeyClient client = getValkeyClient(clientId);
					if (client != null) {
						ClientSummary summary = ValkeyClientSummaryMapper.INSTANCE
								.valkeyClientToValkeyClientSummary(client);
						String cursor = Base64.getEncoder()
								.encodeToString(
										String.valueOf(index).getBytes(StandardCharsets.UTF_8));
						edges.add(ClientSummaryEdge.builder().cursor(cursor).node(summary).build());
					}
					index++;
				}
			}
		}

		String startCursor = !edges.isEmpty() ? edges.get(0).getCursor() : null;
		String endCursor = !edges.isEmpty() ? edges.get(edges.size() - 1).getCursor() : null;

		PageInfo pageInfo = PageInfo.builder().hasNextPage(hasNextPage)
				.hasPreviousPage(hasPreviousPage).startCursor(startCursor).endCursor(endCursor)
				.build();

		return ClientSummaryConnection.builder().edges(edges).pageInfo(pageInfo).build();
	}

	@Override
	public Client getClient(@NonNull String clientId) {
		return getValkeyClient(clientId);
	}

	@Override
	public void deleteClient(@NonNull String clientId) {
		redisTemplate.delete(clientKey(clientId));
		redisTemplate.opsForZSet().remove(CLIENTS_INDEX_KEY, clientId);
	}

	@Override
	public void putClient(@NonNull String clientId, @NonNull String description,
			@NonNull String salt, @NonNull ClientType clientType,
			@NonNull List<ClientScope> availableScopes) {

		String key = clientKey(clientId);

		if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
			throw new BadRequestException("client record already exists");
		}

		String now = TimeUtil.getCurrentTimestamp(this.epochTimeProvider.epochTimeSeconds());

		List<ClientScope> availableScopesList = new ArrayList<>();
		for (ClientScope scope : availableScopes) {
			availableScopesList
					.add(ClientScopeMapper.INSTANCE.clientScopeToValkeyClientScope(scope));
		}

		ValkeyClient record = ValkeyClient.builder().clientCreated(now).clientId(clientId)
				.description(description).salt(salt).clientType(clientType)
				.availableScopes(Collections.unmodifiableList(availableScopesList)).build();

		saveClientToHash(key, record);
		redisTemplate.opsForZSet().add(CLIENTS_INDEX_KEY, clientId, 0);
	}

	@Override
	public void addClientAvailableScope(@NonNull String clientId,
			@NonNull ClientScope availableScope) {
		String key = clientKey(clientId);
		ValkeyClient record = getValkeyClient(clientId);
		if (record == null) {
			throw new NotFoundException("client not found");
		}

		ValkeyClientScope scope =
				ClientScopeMapper.INSTANCE.clientScopeToValkeyClientScope(availableScope);

		List<ClientScope> list = record.getAvailableScopes();
		if (list == null) {
			list = new ArrayList<>();
		} else {
			list = new ArrayList<>(list);
		}

		for (ClientScope cs : list) {
			if (scope.getScope().equals(cs.getScope())) {
				throw new BadRequestException("duplicate scope");
			}
		}

		list.add(scope);

		record = record.toBuilder().availableScopes(Collections.unmodifiableList(list)).build();
		saveClientToHash(key, record);
	}

	@Override
	public void addAuthorizedJwt(@NonNull String clientId, @NonNull String id,
			@NonNull String jwksUrl, @NonNull String iss, @NonNull String sub,
			@NonNull String aud) {
		String key = clientKey(clientId);
		ValkeyClient record = getValkeyClient(clientId);
		if (record == null) {
			throw new NotFoundException("client not found");
		}

		ValkeyClientJwtBearer jwt = ValkeyClientJwtBearer.builder().id(id).jwksUrl(jwksUrl).iss(iss)
				.sub(sub).aud(aud).build();

		List<ClientJwtBearer> list = record.getJwtBearer();

		if (list == null) {
			list = new ArrayList<>();
		} else {
			for (ClientJwtBearer cjb : list) {
				if (jwt.matches(cjb)) {
					throw new BadRequestException("duplicate authorization");
				}
			}
			list = new ArrayList<>(list);
		}

		list.add(jwt);

		record = record.toBuilder().jwtBearer(Collections.unmodifiableList(list)).build();
		saveClientToHash(key, record);
	}

	@Override
	public void removeAuthorizedJwt(@NonNull String clientId, @NonNull String id) {
		String key = clientKey(clientId);
		ValkeyClient record = getValkeyClient(clientId);
		if (record == null) {
			throw new NotFoundException("client not found");
		}

		ClientJwtBearer match = null;
		if (record.getJwtBearer() != null) {
			for (ClientJwtBearer cjb : record.getJwtBearer()) {
				if (id.equals(cjb.getId())) {
					match = cjb;
				}
			}
		}

		if (match != null) {
			List<ClientJwtBearer> list = new ArrayList<>(record.getJwtBearer());
			list.remove(match);

			record = record.toBuilder().jwtBearer(list).build();
			saveClientToHash(key, record);
		}
	}

	@Override
	public void saveClientSecret1(@NonNull String clientId, @NonNull String hashedSecret) {
		String key = clientKey(clientId);
		ValkeyClient record = getValkeyClient(clientId);
		if (record == null) {
			throw new NotFoundException("client not found");
		}

		String now = TimeUtil.getCurrentTimestamp(this.epochTimeProvider.epochTimeSeconds());

		record =
				record.toBuilder().clientSecret1(hashedSecret).clientSecret1Updated(now).build();
		saveClientToHash(key, record);
	}

	@Override
	public void saveClientSecret2(@NonNull String clientId, @NonNull String hashedSecret) {
		String key = clientKey(clientId);
		ValkeyClient record = getValkeyClient(clientId);
		if (record == null) {
			throw new NotFoundException("client not found");
		}

		String now = TimeUtil.getCurrentTimestamp(this.epochTimeProvider.epochTimeSeconds());

		record =
				record.toBuilder().clientSecret2(hashedSecret).clientSecret2Updated(now).build();
		saveClientToHash(key, record);
	}

	@Override
	public void clearClientSecret1(@NonNull String clientId) {
		String key = clientKey(clientId);
		ValkeyClient record = getValkeyClient(clientId);
		if (record == null) {
			throw new NotFoundException("client not found");
		}

		String now = TimeUtil.getCurrentTimestamp(this.epochTimeProvider.epochTimeSeconds());

		record = record.toBuilder().clientSecret1(null).clientSecret1Updated(now).build();
		saveClientToHash(key, record);
	}

	@Override
	public void clearClientSecret2(@NonNull String clientId) {
		String key = clientKey(clientId);
		ValkeyClient record = getValkeyClient(clientId);
		if (record == null) {
			throw new NotFoundException("client not found");
		}

		String now = TimeUtil.getCurrentTimestamp(this.epochTimeProvider.epochTimeSeconds());

		record = record.toBuilder().clientSecret2(null).clientSecret2Updated(now).build();
		saveClientToHash(key, record);
	}

	private ValkeyClient getValkeyClient(String clientId) {
		String key = clientKey(clientId);
		Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
		if (entries == null || entries.isEmpty()) {
			return null;
		}
		return hashToClient(entries);
	}

	private void saveClientToHash(String key, ValkeyClient client) {
		// Delete the key first to ensure a clean state
		redisTemplate.delete(key);

		redisTemplate.opsForHash().put(key, "clientId", nullSafe(client.getClientId()));
		redisTemplate.opsForHash().put(key, "clientCreated", nullSafe(client.getClientCreated()));
		redisTemplate.opsForHash().put(key, "description", nullSafe(client.getDescription()));
		redisTemplate.opsForHash().put(key, "salt", nullSafe(client.getSalt()));
		redisTemplate.opsForHash().put(key, "clientType",
				client.getClientType() != null ? client.getClientType().name() : "");
		redisTemplate.opsForHash().put(key, "clientSecret1",
				nullSafe(client.getClientSecret1()));
		redisTemplate.opsForHash().put(key, "clientSecret1Updated",
				nullSafe(client.getClientSecret1Updated()));
		redisTemplate.opsForHash().put(key, "clientSecret2",
				nullSafe(client.getClientSecret2()));
		redisTemplate.opsForHash().put(key, "clientSecret2Updated",
				nullSafe(client.getClientSecret2Updated()));
		redisTemplate.opsForHash().put(key, "locked",
				client.getLocked() != null ? client.getLocked().toString() : "");

		try {
			String scopesJson = OBJECT_MAPPER.writeValueAsString(serializeScopes(client));
			redisTemplate.opsForHash().put(key, "availableScopes", scopesJson);

			String jwtBearerJson =
					OBJECT_MAPPER.writeValueAsString(serializeJwtBearers(client));
			redisTemplate.opsForHash().put(key, "jwtBearer", jwtBearerJson);
		} catch (JsonProcessingException e) {
			throw new InternalServerErrorException("Failed to serialize client data");
		}
	}

	private List<Map<String, String>> serializeScopes(ValkeyClient client) {
		List<Map<String, String>> result = new ArrayList<>();
		if (client.getAvailableScopes() != null) {
			for (ClientScope scope : client.getAvailableScopes()) {
				result.add(Map.of("scope", nullSafe(scope.getScope()), "description",
						nullSafe(scope.getDescription())));
			}
		}
		return result;
	}

	private List<Map<String, String>> serializeJwtBearers(ValkeyClient client) {
		List<Map<String, String>> result = new ArrayList<>();
		if (client.getJwtBearer() != null) {
			for (ClientJwtBearer jwt : client.getJwtBearer()) {
				result.add(Map.of("id", nullSafe(jwt.getId()), "jwksUrl",
						nullSafe(jwt.getJwksUrl()), "iss", nullSafe(jwt.getIss()), "sub",
						nullSafe(jwt.getSub()), "aud", nullSafe(jwt.getAud())));
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private ValkeyClient hashToClient(Map<Object, Object> entries) {
		String clientId = getStr(entries, "clientId");
		String clientCreated = getStr(entries, "clientCreated");
		String description = getStr(entries, "description");
		String salt = getStr(entries, "salt");
		String clientTypeStr = getStr(entries, "clientType");
		ClientType clientType =
				clientTypeStr != null && !clientTypeStr.isEmpty()
						? ClientType.valueOf(clientTypeStr)
						: null;
		String clientSecret1 = getStr(entries, "clientSecret1");
		String clientSecret1Updated = getStr(entries, "clientSecret1Updated");
		String clientSecret2 = getStr(entries, "clientSecret2");
		String clientSecret2Updated = getStr(entries, "clientSecret2Updated");
		String lockedStr = getStr(entries, "locked");
		Boolean locked =
				lockedStr != null && !lockedStr.isEmpty() ? Boolean.valueOf(lockedStr) : null;

		List<ClientScope> availableScopes = new ArrayList<>();
		String scopesJson = getStr(entries, "availableScopes");
		if (scopesJson != null && !scopesJson.isEmpty()) {
			try {
				List<Map<String, String>> scopesList = OBJECT_MAPPER.readValue(scopesJson,
						new TypeReference<List<Map<String, String>>>() {
						});
				for (Map<String, String> s : scopesList) {
					availableScopes.add(ValkeyClientScope.builder()
							.scope(s.get("scope"))
							.description(s.get("description")).build());
				}
			} catch (JsonProcessingException e) {
				throw new InternalServerErrorException("Failed to deserialize scopes");
			}
		}

		List<ClientJwtBearer> jwtBearers = new ArrayList<>();
		String jwtBearerJson = getStr(entries, "jwtBearer");
		if (jwtBearerJson != null && !jwtBearerJson.isEmpty()) {
			try {
				List<Map<String, String>> jwtList = OBJECT_MAPPER.readValue(jwtBearerJson,
						new TypeReference<List<Map<String, String>>>() {
						});
				for (Map<String, String> j : jwtList) {
					jwtBearers.add(ValkeyClientJwtBearer.builder()
							.id(j.get("id"))
							.jwksUrl(j.get("jwksUrl"))
							.iss(j.get("iss"))
							.sub(j.get("sub"))
							.aud(j.get("aud")).build());
				}
			} catch (JsonProcessingException e) {
				throw new InternalServerErrorException("Failed to deserialize JWT bearers");
			}
		}

		return ValkeyClient.builder().clientId(clientId).clientCreated(clientCreated)
				.description(description).salt(salt).clientType(clientType)
				.clientSecret1(clientSecret1).clientSecret1Updated(clientSecret1Updated)
				.clientSecret2(clientSecret2).clientSecret2Updated(clientSecret2Updated)
				.availableScopes(Collections.unmodifiableList(availableScopes))
				.jwtBearer(Collections.unmodifiableList(jwtBearers)).locked(locked).build();
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
