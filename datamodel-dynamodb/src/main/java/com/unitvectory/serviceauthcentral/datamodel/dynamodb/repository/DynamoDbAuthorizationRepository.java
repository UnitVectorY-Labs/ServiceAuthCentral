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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.unitvectory.consistgen.epoch.EpochTimeProvider;
import com.unitvectory.serviceauthcentral.datamodel.dynamodb.model.AuthorizationRecord;
import com.unitvectory.serviceauthcentral.datamodel.model.Authorization;
import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.time.TimeUtil;
import com.unitvectory.serviceauthcentral.util.HashingUtil;
import com.unitvectory.serviceauthcentral.util.exception.BadRequestException;
import com.unitvectory.serviceauthcentral.util.exception.NotFoundException;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

/**
 * The DynamoDB Authorization Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@AllArgsConstructor
public class DynamoDbAuthorizationRepository implements AuthorizationRepository {

	private DynamoDbEnhancedClient dynamoDbEnhancedClient;

	private String tableName;

	private EpochTimeProvider epochTimeProvider;

	private DynamoDbTable<AuthorizationRecord> getTable() {
		return dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(AuthorizationRecord.class));
	}

	private String getDocumentId(@NonNull String subject, @NonNull String audience) {
		String subjectHash = HashingUtil.sha256(subject);
		String audienceHash = HashingUtil.sha256(audience);
		return HashingUtil.sha256(subjectHash + audienceHash);
	}

	@Override
	public Authorization getAuthorization(@NonNull String id) {
		DynamoDbTable<AuthorizationRecord> table = getTable();
		Key key = Key.builder().partitionValue(id).build();
		return table.getItem(key);
	}

	@Override
	public void deleteAuthorization(@NonNull String id) {
		DynamoDbTable<AuthorizationRecord> table = getTable();
		Key key = Key.builder().partitionValue(id).build();
		table.deleteItem(key);
	}

	@Override
	public Authorization getAuthorization(@NonNull String subject, @NonNull String audience) {
		String documentId = getDocumentId(subject, audience);
		return getAuthorization(documentId);
	}

	@Override
	public Iterator<Authorization> getAuthorizationBySubject(@NonNull String subject) {
		DynamoDbTable<AuthorizationRecord> table = getTable();
		DynamoDbIndex<AuthorizationRecord> index = table.index("subject-index");

		QueryConditional queryConditional = QueryConditional
				.keyEqualTo(Key.builder().partitionValue(subject).build());

		QueryEnhancedRequest request = QueryEnhancedRequest.builder()
				.queryConditional(queryConditional)
				.build();

		ArrayList<Authorization> list = new ArrayList<>();
		for (Page<AuthorizationRecord> page : index.query(request)) {
			for (AuthorizationRecord record : page.items()) {
				list.add(record);
			}
		}

		return list.iterator();
	}

	@Override
	public Iterator<Authorization> getAuthorizationByAudience(@NonNull String audience) {
		DynamoDbTable<AuthorizationRecord> table = getTable();
		DynamoDbIndex<AuthorizationRecord> index = table.index("audience-index");

		QueryConditional queryConditional = QueryConditional
				.keyEqualTo(Key.builder().partitionValue(audience).build());

		QueryEnhancedRequest request = QueryEnhancedRequest.builder()
				.queryConditional(queryConditional)
				.build();

		ArrayList<Authorization> list = new ArrayList<>();
		for (Page<AuthorizationRecord> page : index.query(request)) {
			for (AuthorizationRecord record : page.items()) {
				list.add(record);
			}
		}

		return list.iterator();
	}

	@Override
	public void authorize(@NonNull String subject, @NonNull String audience,
			@NonNull List<String> authorizedScopes) {
		String now = TimeUtil.getCurrentTimestamp(this.epochTimeProvider.epochTimeSeconds());
		String documentId = getDocumentId(subject, audience);

		AuthorizationRecord record = AuthorizationRecord.builder()
				.pk(documentId)
				.authorizationCreated(now)
				.subject(subject)
				.audience(audience)
				.authorizedScopes(authorizedScopes)
				.build();

		DynamoDbTable<AuthorizationRecord> table = getTable();
		table.putItem(record);
	}

	@Override
	public void deauthorize(@NonNull String subject, @NonNull String audience) {
		String documentId = getDocumentId(subject, audience);
		DynamoDbTable<AuthorizationRecord> table = getTable();
		Key key = Key.builder().partitionValue(documentId).build();
		table.deleteItem(key);
	}

	@Override
	public void authorizeAddScope(@NonNull String subject, @NonNull String audience,
			@NonNull String authorizedScope) {
		String documentId = getDocumentId(subject, audience);
		DynamoDbTable<AuthorizationRecord> table = getTable();
		Key key = Key.builder().partitionValue(documentId).build();

		AuthorizationRecord record = table.getItem(key);
		if (record == null) {
			throw new NotFoundException("Authorization not found");
		}

		if (record.getAuthorizedScopes().contains(authorizedScope)) {
			throw new BadRequestException("Scope already authorized");
		}

		List<String> authorizedScopesList = new ArrayList<>();
		if (record.getAuthorizedScopes() != null) {
			authorizedScopesList.addAll(record.getAuthorizedScopes());
		}

		authorizedScopesList.add(authorizedScope);
		record.setAuthorizedScopes(authorizedScopesList);
		table.putItem(record);
	}

	@Override
	public void authorizeRemoveScope(@NonNull String subject, @NonNull String audience,
			@NonNull String authorizedScope) {
		String documentId = getDocumentId(subject, audience);
		DynamoDbTable<AuthorizationRecord> table = getTable();
		Key key = Key.builder().partitionValue(documentId).build();

		AuthorizationRecord record = table.getItem(key);
		if (record == null) {
			throw new NotFoundException("Authorization not found");
		}

		List<String> authorizedScopesList = new ArrayList<>();
		if (record.getAuthorizedScopes() != null) {
			authorizedScopesList.addAll(record.getAuthorizedScopes());
		}

		authorizedScopesList.remove(authorizedScope);
		record.setAuthorizedScopes(authorizedScopesList);
		table.putItem(record);
	}
}
