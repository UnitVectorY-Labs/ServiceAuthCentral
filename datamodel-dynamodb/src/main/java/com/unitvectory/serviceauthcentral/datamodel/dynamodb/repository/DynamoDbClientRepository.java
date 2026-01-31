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
package com.unitvectory.serviceauthcentral.datamodel.dynamodb.repository;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.unitvectory.consistgen.epoch.EpochTimeProvider;
import com.unitvectory.serviceauthcentral.datamodel.dynamodb.mapper.ClientScopeMapper;
import com.unitvectory.serviceauthcentral.datamodel.dynamodb.model.ClientJwtBearerRecord;
import com.unitvectory.serviceauthcentral.datamodel.dynamodb.model.ClientRecord;
import com.unitvectory.serviceauthcentral.datamodel.dynamodb.model.ClientScopeRecord;
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
import com.unitvectory.serviceauthcentral.util.HashingUtil;
import com.unitvectory.serviceauthcentral.util.exception.BadRequestException;
import com.unitvectory.serviceauthcentral.util.exception.ConflictException;
import com.unitvectory.serviceauthcentral.util.exception.NotFoundException;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * The DynamoDB Client Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@AllArgsConstructor
public class DynamoDbClientRepository implements ClientRepository {

	private DynamoDbEnhancedClient dynamoDbEnhancedClient;

	private String tableName;

	private EpochTimeProvider epochTimeProvider;

	private DynamoDbTable<ClientRecord> getTable() {
		return dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(ClientRecord.class));
	}

	@Override
	public ClientSummaryConnection getClients(Integer first, String after, Integer last,
			String before) {
		DynamoDbTable<ClientRecord> table = getTable();
		List<ClientSummaryEdge> edges = new ArrayList<>();
		boolean hasPreviousPage = false;
		boolean hasNextPage = false;

		// For DynamoDB, we'll use scan with pagination
		// Forward pagination
		if (first != null) {
			Map<String, AttributeValue> exclusiveStartKey = null;
			if (after != null && !after.isEmpty()) {
				String afterDecoded = new String(Base64.getDecoder().decode(after), StandardCharsets.UTF_8);
				String pk = HashingUtil.sha256(afterDecoded);
				exclusiveStartKey = new HashMap<>();
				exclusiveStartKey.put("pk", AttributeValue.builder().s(pk).build());
				hasPreviousPage = true;
			}

			ScanEnhancedRequest.Builder requestBuilder = ScanEnhancedRequest.builder()
					.limit(first + 1);

			if (exclusiveStartKey != null) {
				requestBuilder.exclusiveStartKey(exclusiveStartKey);
			}

			for (Page<ClientRecord> page : table.scan(requestBuilder.build())) {
				for (ClientRecord record : page.items()) {
					if (edges.size() >= first + 1) {
						break;
					}
					String cursor = Base64.getEncoder().encodeToString(
							record.getClientId().getBytes(StandardCharsets.UTF_8));
					ClientSummary summary = new ClientSummary() {
						@Override
						public String getClientId() {
							return record.getClientId();
						}

						@Override
						public String getDescription() {
							return record.getDescription();
						}
					};
					edges.add(ClientSummaryEdge.builder().node(summary).cursor(cursor).build());
				}
				if (edges.size() >= first + 1) {
					break;
				}
			}

			if (edges.size() > first) {
				hasNextPage = true;
				edges.remove(edges.size() - 1);
			}
		}

		// Backward pagination
		if (last != null) {
			// DynamoDB doesn't natively support backward scanning, so we'll scan and reverse
			List<ClientSummaryEdge> allEdges = new ArrayList<>();
			for (Page<ClientRecord> page : table.scan()) {
				for (ClientRecord record : page.items()) {
					String cursor = Base64.getEncoder().encodeToString(
							record.getClientId().getBytes(StandardCharsets.UTF_8));
					ClientSummary summary = new ClientSummary() {
						@Override
						public String getClientId() {
							return record.getClientId();
						}

						@Override
						public String getDescription() {
							return record.getDescription();
						}
					};
					allEdges.add(ClientSummaryEdge.builder().node(summary).cursor(cursor).build());
				}
			}

			// Sort by clientId
			allEdges.sort((a, b) -> a.getNode().getClientId().compareTo(b.getNode().getClientId()));
			Collections.reverse(allEdges);

			if (before != null && !before.isEmpty()) {
				String beforeDecoded = new String(Base64.getDecoder().decode(before), StandardCharsets.UTF_8);
				int index = -1;
				for (int i = 0; i < allEdges.size(); i++) {
					if (allEdges.get(i).getNode().getClientId().equals(beforeDecoded)) {
						index = i;
						break;
					}
				}
				if (index >= 0) {
					allEdges = allEdges.subList(index + 1, allEdges.size());
					hasNextPage = true;
				}
			}

			if (allEdges.size() > last) {
				hasPreviousPage = true;
				allEdges = allEdges.subList(allEdges.size() - last, allEdges.size());
			}

			edges = allEdges;
			Collections.reverse(edges);
		}

		PageInfo pageInfo = constructPageInfo(edges, hasPreviousPage, hasNextPage);
		return ClientSummaryConnection.builder().edges(edges).pageInfo(pageInfo).build();
	}

	private PageInfo constructPageInfo(List<ClientSummaryEdge> edges, boolean hasPreviousPage,
			boolean hasNextPage) {
		String startCursor = null, endCursor = null;
		if (!edges.isEmpty()) {
			startCursor = edges.get(0).getCursor();
			endCursor = edges.get(edges.size() - 1).getCursor();
		}
		return PageInfo.builder().hasNextPage(hasNextPage).hasPreviousPage(hasPreviousPage)
				.startCursor(startCursor).endCursor(endCursor).build();
	}

	@Override
	public Client getClient(@NonNull String clientId) {
		String pk = HashingUtil.sha256(clientId);
		DynamoDbTable<ClientRecord> table = getTable();
		Key key = Key.builder().partitionValue(pk).build();
		return table.getItem(key);
	}

	@Override
	public void deleteClient(@NonNull String clientId) {
		String pk = HashingUtil.sha256(clientId);
		DynamoDbTable<ClientRecord> table = getTable();
		Key key = Key.builder().partitionValue(pk).build();
		table.deleteItem(key);
	}

	@Override
	public void putClient(@NonNull String clientId, String description, @NonNull String salt,
			@NonNull ClientType clientType, @NonNull List<ClientScope> availableScopes) {
		String pk = HashingUtil.sha256(clientId);
		String now = TimeUtil.getCurrentTimestamp(this.epochTimeProvider.epochTimeSeconds());

		ClientRecord record = ClientRecord.builder()
				.pk(pk)
				.clientCreated(now)
				.clientId(clientId)
				.description(description)
				.salt(salt)
				.clientType(clientType)
				.availableScopes(ClientScopeMapper.INSTANCE
						.clientScopeToClientScopeRecord(availableScopes))
				.build();

		DynamoDbTable<ClientRecord> table = getTable();

		// Use a condition expression to only put if the item doesn't exist
		Expression conditionExpression = Expression.builder()
				.expression("attribute_not_exists(pk)")
				.build();

		PutItemEnhancedRequest<ClientRecord> request = PutItemEnhancedRequest.builder(ClientRecord.class)
				.item(record)
				.conditionExpression(conditionExpression)
				.build();

		try {
			table.putItem(request);
		} catch (software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException e) {
			throw new ConflictException("clientId already exists");
		}
	}

	@Override
	public void addClientAvailableScope(@NonNull String clientId, @NonNull ClientScope availableScope) {
		String pk = HashingUtil.sha256(clientId);
		DynamoDbTable<ClientRecord> table = getTable();
		Key key = Key.builder().partitionValue(pk).build();

		ClientRecord record = table.getItem(key);
		if (record == null) {
			throw new NotFoundException("Client not found");
		}

		List<ClientScope> availableScopesOriginal = record.getAvailableScopes();
		List<ClientScopeRecord> availableScopesList = new ArrayList<>();
		for (ClientScope scope : availableScopesOriginal) {
			if (scope.getScope().equals(availableScope.getScope())) {
				throw new BadRequestException("Duplicate scope");
			}
			availableScopesList.add(ClientScopeMapper.INSTANCE.clientScopeToClientScopeRecord(scope));
		}

		availableScopesList.add(ClientScopeMapper.INSTANCE.clientScopeToClientScopeRecord(availableScope));
		record.setAvailableScopes(availableScopesList);
		table.putItem(record);
	}

	@Override
	public void addAuthorizedJwt(@NonNull String clientId, @NonNull String id,
			@NonNull String jwksUrl, @NonNull String iss, @NonNull String sub,
			@NonNull String aud) {
		String pk = HashingUtil.sha256(clientId);
		DynamoDbTable<ClientRecord> table = getTable();
		Key key = Key.builder().partitionValue(pk).build();

		ClientRecord record = table.getItem(key);
		if (record == null) {
			throw new NotFoundException("Client not found");
		}

		List<ClientJwtBearer> jwtBearerListOriginal = record.getJwtBearer();
		List<ClientJwtBearerRecord> jwtBearerList = new ArrayList<>();

		if (jwtBearerListOriginal != null) {
			for (ClientJwtBearer cjb : jwtBearerListOriginal) {
				jwtBearerList.add(ClientJwtBearerRecord.builder()
						.id(cjb.getId())
						.jwksUrl(cjb.getJwksUrl())
						.iss(cjb.getIss())
						.sub(cjb.getSub())
						.aud(cjb.getAud())
						.build());
			}
		}

		ClientJwtBearerRecord newJwt = ClientJwtBearerRecord.builder()
				.id(id)
				.jwksUrl(jwksUrl)
				.iss(iss)
				.sub(sub)
				.aud(aud)
				.build();

		// Check for duplicates
		for (ClientJwtBearer cjb : jwtBearerList) {
			if (newJwt.matches(cjb)) {
				throw new BadRequestException("Duplicate authorization");
			}
		}

		jwtBearerList.add(newJwt);
		record.setJwtBearer(jwtBearerList);
		table.putItem(record);
	}

	@Override
	public void removeAuthorizedJwt(@NonNull String clientId, @NonNull String id) {
		String pk = HashingUtil.sha256(clientId);
		DynamoDbTable<ClientRecord> table = getTable();
		Key key = Key.builder().partitionValue(pk).build();

		ClientRecord record = table.getItem(key);
		if (record == null) {
			throw new NotFoundException("Client not found");
		}

		List<ClientJwtBearer> jwtBearerListOriginal = record.getJwtBearer();
		List<ClientJwtBearerRecord> jwtBearerList = new ArrayList<>();

		if (jwtBearerListOriginal != null) {
			boolean found = false;
			for (ClientJwtBearer cjb : jwtBearerListOriginal) {
				if (id.equals(cjb.getId())) {
					found = true;
					continue;
				}
				jwtBearerList.add(ClientJwtBearerRecord.builder()
						.id(cjb.getId())
						.jwksUrl(cjb.getJwksUrl())
						.iss(cjb.getIss())
						.sub(cjb.getSub())
						.aud(cjb.getAud())
						.build());
			}

			if (!found) {
				throw new NotFoundException("JWT not found with the provided id");
			}
		}

		record.setJwtBearer(jwtBearerList);
		table.putItem(record);
	}

	@Override
	public void saveClientSecret1(@NonNull String clientId, @NonNull String hashedSecret) {
		String now = TimeUtil.getCurrentTimestamp(this.epochTimeProvider.epochTimeSeconds());
		String pk = HashingUtil.sha256(clientId);
		DynamoDbTable<ClientRecord> table = getTable();
		Key key = Key.builder().partitionValue(pk).build();

		ClientRecord record = table.getItem(key);
		if (record != null) {
			record.setClientSecret1(hashedSecret);
			record.setClientSecret1Updated(now);
			table.putItem(record);
		}
	}

	@Override
	public void saveClientSecret2(@NonNull String clientId, @NonNull String hashedSecret) {
		String now = TimeUtil.getCurrentTimestamp(this.epochTimeProvider.epochTimeSeconds());
		String pk = HashingUtil.sha256(clientId);
		DynamoDbTable<ClientRecord> table = getTable();
		Key key = Key.builder().partitionValue(pk).build();

		ClientRecord record = table.getItem(key);
		if (record != null) {
			record.setClientSecret2(hashedSecret);
			record.setClientSecret2Updated(now);
			table.putItem(record);
		}
	}

	@Override
	public void clearClientSecret1(@NonNull String clientId) {
		String now = TimeUtil.getCurrentTimestamp(this.epochTimeProvider.epochTimeSeconds());
		String pk = HashingUtil.sha256(clientId);
		DynamoDbTable<ClientRecord> table = getTable();
		Key key = Key.builder().partitionValue(pk).build();

		ClientRecord record = table.getItem(key);
		if (record != null) {
			record.setClientSecret1(null);
			record.setClientSecret1Updated(now);
			table.putItem(record);
		}
	}

	@Override
	public void clearClientSecret2(@NonNull String clientId) {
		String now = TimeUtil.getCurrentTimestamp(this.epochTimeProvider.epochTimeSeconds());
		String pk = HashingUtil.sha256(clientId);
		DynamoDbTable<ClientRecord> table = getTable();
		Key key = Key.builder().partitionValue(pk).build();

		ClientRecord record = table.getItem(key);
		if (record != null) {
			record.setClientSecret2(null);
			record.setClientSecret2Updated(now);
			table.putItem(record);
		}
	}
}
