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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.unitvectory.consistgen.epoch.EpochTimeProvider;
import com.unitvectory.serviceauthcentral.datamodel.memory.mapper.ClientScopeMapper;
import com.unitvectory.serviceauthcentral.datamodel.memory.mapper.MemoryClientSummaryMapper;
import com.unitvectory.serviceauthcentral.datamodel.memory.model.MemoryClient;
import com.unitvectory.serviceauthcentral.datamodel.memory.model.MemoryClientJwtBearer;
import com.unitvectory.serviceauthcentral.datamodel.memory.model.MemoryClientScope;
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
import com.unitvectory.serviceauthcentral.util.exception.BadRequestException;
import com.unitvectory.serviceauthcentral.util.exception.NotFoundException;

import lombok.NonNull;
import lombok.Synchronized;

/**
 * The Memory Client Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public class MemoryClientRepository implements ClientRepository {

	private EpochTimeProvider epochTimeProvider;

	private Map<String, MemoryClient> memory;

	public MemoryClientRepository(EpochTimeProvider epochTimeProvider) {
		this.memory = new TreeMap<>();
		this.epochTimeProvider = epochTimeProvider;
	}

	public void reset() {
		this.memory.clear();
	}

	@Override
	public ClientSummaryConnection getClients(Integer first, String after, Integer last,
			String before) {
		List<ClientSummaryEdge> edges = new ArrayList<>();
		boolean hasNextPage = false;
		boolean hasPreviousPage = false;

		// Convert the cursors from Base64 to integer index (or vice versa)
		Integer afterIndex = after != null
				? Integer.parseInt(
						new String(Base64.getDecoder().decode(after), StandardCharsets.UTF_8))
				: null;
		Integer beforeIndex = before != null
				? Integer.parseInt(
						new String(Base64.getDecoder().decode(before), StandardCharsets.UTF_8))
				: null;

		// Determine the range of data to fetch
		int startIndex = 0;
		int endIndex = memory.size(); // exclusive
		if (first != null && afterIndex != null) {
			startIndex = afterIndex;
			endIndex = Math.min(startIndex + first, memory.size());
			hasNextPage = endIndex < memory.size();
			hasPreviousPage = startIndex > 0;
		} else if (last != null && beforeIndex != null) {
			endIndex = beforeIndex;
			startIndex = Math.max(endIndex - last, 0);
			hasNextPage = endIndex < memory.size();
			hasPreviousPage = startIndex > 0;
		} else if (first != null) {
			endIndex = Math.min(first, memory.size());
			hasNextPage = endIndex < memory.size();
		} else if (last != null) {
			startIndex = Math.max(memory.size() - last, 0);
			hasPreviousPage = startIndex > 0;
		}

		// Fetching the data
		List<MemoryClient> clientsList = new ArrayList<>(memory.values());
		for (int i = startIndex; i < endIndex; i++) {
			MemoryClient client = clientsList.get(i);
			ClientSummary summary = MemoryClientSummaryMapper.INSTANCE.memoryClientToMemoryClientSummary(client);
			String cursor = Base64.getEncoder()
					.encodeToString(String.valueOf(i).getBytes(StandardCharsets.UTF_8));
			edges.add(ClientSummaryEdge.builder().cursor(cursor).node(summary).build());
		}

		// Determine cursors for pageInfo
		String startCursor = !edges.isEmpty() ? edges.get(0).getCursor() : null;
		String endCursor = !edges.isEmpty() ? edges.get(edges.size() - 1).getCursor() : null;

		PageInfo pageInfo = PageInfo.builder().hasNextPage(hasNextPage).hasPreviousPage(hasPreviousPage)
				.startCursor(startCursor).endCursor(endCursor).build();

		return ClientSummaryConnection.builder().edges(edges).pageInfo(pageInfo).build();
	}

	@Override
	public Client getClient(@NonNull String clientId) {
		return this.memory.get(clientId);
	}

	@Override
	@Synchronized
	public void putClient(@NonNull String clientId, @NonNull String description,
			@NonNull String salt, @NonNull ClientType clientType,
			@NonNull List<ClientScope> availableScopes) {

		String now = TimeUtil.getCurrentTimestamp(this.epochTimeProvider.epochTimeSeconds());

		List<ClientScope> availableScopesList = new ArrayList<>();
		for (ClientScope scope : availableScopes) {
			availableScopesList
					.add(ClientScopeMapper.INSTANCE.clientScopeToMemoryClientScope(scope));
		}

		MemoryClient record = this.memory.get(clientId);
		if (record == null) {
			record = MemoryClient.builder().clientCreated(now)
					.clientId(clientId).description(description).salt(salt)
					.availableScopes(Collections.unmodifiableList(availableScopesList)).build();
			this.memory.put(clientId, record);
		} else {
			throw new BadRequestException("client record already exists");
		}
	}

	@Override
	public void addClientAvailableScope(String clientId, ClientScope availableScope) {
		MemoryClient record = this.memory.get(clientId);
		if (record == null) {
			throw new NotFoundException("client not found");
		}

		// What we are trying to add
		MemoryClientScope scope = ClientScopeMapper.INSTANCE.clientScopeToMemoryClientScope(availableScope);

		// Make a copy of the array that can be edited
		List<ClientScope> list = record.getAvailableScopes();
		if (list == null) {
			list = new ArrayList<>();
		} else {
			list = new ArrayList<>();
			list.addAll(record.getAvailableScopes());
		}

		// Check for duplicates
		for (ClientScope cs : list) {
			if (scope.getScope().equals(cs.getScope())) {
				throw new BadRequestException("duplicate scope");
			}
		}

		list.add(scope);

		// Clone the record, modify the list
		record = record.toBuilder().availableScopes(Collections.unmodifiableList(list)).build();
		this.memory.put(clientId, record);
	}

	@Override
	public void deleteClient(@NonNull String clientId) {
		this.memory.remove(clientId);
	}

	@Override
	@Synchronized
	public void addAuthorizedJwt(@NonNull String clientId, @NonNull String id,
			@NonNull String jwksUrl, @NonNull String iss, @NonNull String sub,
			@NonNull String aud) {
		MemoryClient record = this.memory.get(clientId);
		if (record == null) {
			throw new NotFoundException("client not found");
		}

		// What we are trying to add
		MemoryClientJwtBearer jwt = MemoryClientJwtBearer.builder().id(id).jwksUrl(jwksUrl).iss(iss)
				.sub(sub).aud(aud).build();

		List<ClientJwtBearer> list = record.getJwtBearer();

		if (list == null) {
			list = new ArrayList<>();
		} else {
			for (ClientJwtBearer cjb : record.getJwtBearer()) {
				if (jwt.matches(cjb)) {
					throw new BadRequestException("duplicate authorization");
				}
			}

			// Make a copy of the array that can be edited
			list = new ArrayList<>();
			list.addAll(record.getJwtBearer());
		}

		list.add(jwt);

		// Clone the record, modify the list
		record = record.toBuilder().jwtBearer(Collections.unmodifiableList(list)).build();
		this.memory.put(clientId, record);
	}

	@Override
	@Synchronized
	public void removeAuthorizedJwt(@NonNull String clientId, @NonNull String id) {
		MemoryClient record = this.memory.get(clientId);
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
			List<ClientJwtBearer> list = new ArrayList<>();
			list.addAll(record.getJwtBearer());
			list.remove(match);

			record = record.toBuilder().jwtBearer(list).build();
			this.memory.put(clientId, record);
		}
	}

	@Override
	@Synchronized
	public void saveClientSecret1(@NonNull String clientId, @NonNull String hashedSecret) {

		String now = TimeUtil.getCurrentTimestamp(this.epochTimeProvider.epochTimeSeconds());

		MemoryClient record = this.memory.get(clientId);
		if (record == null) {
			throw new NotFoundException("client not found");
		}

		record = record.toBuilder().clientSecret1(hashedSecret)
				.clientSecret1Updated(now).build();
		this.memory.put(clientId, record);
	}

	@Override
	@Synchronized
	public void saveClientSecret2(@NonNull String clientId, @NonNull String hashedSecret) {

		String now = TimeUtil.getCurrentTimestamp(this.epochTimeProvider.epochTimeSeconds());

		MemoryClient record = this.memory.get(clientId);
		if (record == null) {
			throw new NotFoundException("client not found");
		}

		record = record.toBuilder().clientSecret2(hashedSecret)
				.clientSecret2Updated(now).build();
		this.memory.put(clientId, record);
	}

	@Override
	@Synchronized
	public void clearClientSecret1(@NonNull String clientId) {

		String now = TimeUtil.getCurrentTimestamp(this.epochTimeProvider.epochTimeSeconds());

		MemoryClient record = this.memory.get(clientId);
		if (record == null) {
			throw new NotFoundException("client not found");
		}

		record = record.toBuilder().clientSecret1(null)
				.clientSecret1Updated(now).build();
		this.memory.put(clientId, record);
	}

	@Override
	@Synchronized
	public void clearClientSecret2(@NonNull String clientId) {

		String now = TimeUtil.getCurrentTimestamp(this.epochTimeProvider.epochTimeSeconds());

		MemoryClient record = this.memory.get(clientId);
		if (record == null) {
			throw new NotFoundException("client not found");
		}

		record = record.toBuilder().clientSecret2(null)
				.clientSecret2Updated(now).build();
		this.memory.put(clientId, record);
	}
}
