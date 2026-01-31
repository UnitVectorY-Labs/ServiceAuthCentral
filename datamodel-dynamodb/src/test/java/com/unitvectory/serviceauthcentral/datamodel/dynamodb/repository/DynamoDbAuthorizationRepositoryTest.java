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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.unitvectory.consistgen.epoch.EpochTimeProvider;
import com.unitvectory.consistgen.epoch.StaticEpochTimeProvider;
import com.unitvectory.serviceauthcentral.datamodel.dynamodb.model.AuthorizationRecord;
import com.unitvectory.serviceauthcentral.datamodel.model.Authorization;
import com.unitvectory.serviceauthcentral.util.HashingUtil;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.ArrayList;

/**
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@SuppressWarnings("unchecked")
public class DynamoDbAuthorizationRepositoryTest {

	private static final String TABLE_AUTHORIZATIONS = "authorizations";

	private final EpochTimeProvider epochTimeProvider = StaticEpochTimeProvider.getInstance();

	@Test
	public void testGetAuthorizationById_Exists() {
		DynamoDbEnhancedClient dynamoDbEnhancedClient = mock(DynamoDbEnhancedClient.class);
		DynamoDbTable<AuthorizationRecord> table = mock(DynamoDbTable.class);

		AuthorizationRecord record = new AuthorizationRecord();
		record.setPk("some-id");
		record.setSubject("subject");
		record.setAudience("audience");

		when(dynamoDbEnhancedClient.table(TABLE_AUTHORIZATIONS, TableSchema.fromBean(AuthorizationRecord.class)))
				.thenReturn(table);
		when(table.getItem((Key) any())).thenReturn(record);

		DynamoDbAuthorizationRepository repository = new DynamoDbAuthorizationRepository(
				dynamoDbEnhancedClient, TABLE_AUTHORIZATIONS, epochTimeProvider);
		Authorization authorization = repository.getAuthorization("some-id");

		assertNotNull(authorization);
	}

	@Test
	public void testGetAuthorizationById_NotExists() {
		DynamoDbEnhancedClient dynamoDbEnhancedClient = mock(DynamoDbEnhancedClient.class);
		DynamoDbTable<AuthorizationRecord> table = mock(DynamoDbTable.class);

		when(dynamoDbEnhancedClient.table(TABLE_AUTHORIZATIONS, TableSchema.fromBean(AuthorizationRecord.class)))
				.thenReturn(table);
		when(table.getItem((Key) any())).thenReturn(null);

		DynamoDbAuthorizationRepository repository = new DynamoDbAuthorizationRepository(
				dynamoDbEnhancedClient, TABLE_AUTHORIZATIONS, epochTimeProvider);
		Authorization authorization = repository.getAuthorization("some-id");

		assertNull(authorization);
	}

	@Test
	public void testAuthorize() {
		DynamoDbEnhancedClient dynamoDbEnhancedClient = mock(DynamoDbEnhancedClient.class);
		DynamoDbTable<AuthorizationRecord> table = mock(DynamoDbTable.class);

		when(dynamoDbEnhancedClient.table(TABLE_AUTHORIZATIONS, TableSchema.fromBean(AuthorizationRecord.class)))
				.thenReturn(table);

		DynamoDbAuthorizationRepository repository = new DynamoDbAuthorizationRepository(
				dynamoDbEnhancedClient, TABLE_AUTHORIZATIONS, epochTimeProvider);
		repository.authorize("subject", "audience", new ArrayList<String>());

		ArgumentCaptor<AuthorizationRecord> argument =
				ArgumentCaptor.forClass(AuthorizationRecord.class);
		verify(table).putItem(argument.capture());
		assertEquals("subject", argument.getValue().getSubject());
		assertEquals("audience", argument.getValue().getAudience());
	}

	@Test
	public void testDeauthorize() {
		DynamoDbEnhancedClient dynamoDbEnhancedClient = mock(DynamoDbEnhancedClient.class);
		DynamoDbTable<AuthorizationRecord> table = mock(DynamoDbTable.class);

		when(dynamoDbEnhancedClient.table(TABLE_AUTHORIZATIONS, TableSchema.fromBean(AuthorizationRecord.class)))
				.thenReturn(table);

		DynamoDbAuthorizationRepository repository = new DynamoDbAuthorizationRepository(
				dynamoDbEnhancedClient, TABLE_AUTHORIZATIONS, epochTimeProvider);
		repository.deauthorize("subject", "audience");

		verify(table).deleteItem((Key) any());
	}
}
