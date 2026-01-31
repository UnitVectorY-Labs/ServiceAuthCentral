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
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.unitvectory.consistgen.epoch.EpochTimeProvider;
import com.unitvectory.consistgen.epoch.StaticEpochTimeProvider;
import com.unitvectory.serviceauthcentral.datamodel.dynamodb.model.ClientRecord;
import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.util.HashingUtil;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

/**
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@SuppressWarnings("unchecked")
public class DynamoDbClientRepositoryTest {

	private static final String TABLE_CLIENTS = "clients";

	private final EpochTimeProvider epochTimeProvider = StaticEpochTimeProvider.getInstance();

	@Test
	public void testNoArgs() {
		DynamoDbClientRepository repository =
				new DynamoDbClientRepository(null, TABLE_CLIENTS, epochTimeProvider);
		assertNotNull(repository);
	}

	@Test
	public void testGetClient_NoClientId() {
		DynamoDbEnhancedClient dynamoDbEnhancedClient = mock(DynamoDbEnhancedClient.class);
		DynamoDbClientRepository repository =
				new DynamoDbClientRepository(dynamoDbEnhancedClient, TABLE_CLIENTS, epochTimeProvider);

		NullPointerException thrown =
				assertThrows(NullPointerException.class, () -> repository.getClient(null),
						"Expected getClient with null clientId to throw exception");

		assertEquals("clientId is marked non-null but is null", thrown.getMessage());
	}

	@Test
	public void testGetClient_InvalidClientId() {
		DynamoDbEnhancedClient dynamoDbEnhancedClient = mock(DynamoDbEnhancedClient.class);
		DynamoDbTable<ClientRecord> table = mock(DynamoDbTable.class);

		when(dynamoDbEnhancedClient.table(TABLE_CLIENTS, TableSchema.fromBean(ClientRecord.class)))
				.thenReturn(table);
		when(table.getItem((Key) any())).thenReturn(null);

		DynamoDbClientRepository repository =
				new DynamoDbClientRepository(dynamoDbEnhancedClient, TABLE_CLIENTS, epochTimeProvider);

		Client client = repository.getClient("invalid-client-id");
		assertNull(client);
	}

	@Test
	public void testGetClient_ClientExists() {
		DynamoDbEnhancedClient dynamoDbEnhancedClient = mock(DynamoDbEnhancedClient.class);
		DynamoDbTable<ClientRecord> table = mock(DynamoDbTable.class);

		ClientRecord clientRecord = new ClientRecord();
		clientRecord.setPk(HashingUtil.sha256("client-id"));
		clientRecord.setClientId("client-id");

		when(dynamoDbEnhancedClient.table(TABLE_CLIENTS, TableSchema.fromBean(ClientRecord.class)))
				.thenReturn(table);
		when(table.getItem((Key) any())).thenReturn(clientRecord);

		DynamoDbClientRepository repository =
				new DynamoDbClientRepository(dynamoDbEnhancedClient, TABLE_CLIENTS, epochTimeProvider);

		Client client = repository.getClient("client-id");
		assertNotNull(client);
		assertEquals("client-id", client.getClientId());
	}
}
