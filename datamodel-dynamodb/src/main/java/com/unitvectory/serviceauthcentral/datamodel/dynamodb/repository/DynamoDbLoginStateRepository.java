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

import com.unitvectory.serviceauthcentral.datamodel.dynamodb.model.LoginStateRecord;
import com.unitvectory.serviceauthcentral.datamodel.model.LoginState;
import com.unitvectory.serviceauthcentral.datamodel.repository.LoginStateRepository;
import com.unitvectory.serviceauthcentral.util.HashingUtil;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

/**
 * The DynamoDB Login State Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@AllArgsConstructor
public class DynamoDbLoginStateRepository implements LoginStateRepository {

	private DynamoDbEnhancedClient dynamoDbEnhancedClient;

	private String tableName;

	private DynamoDbTable<LoginStateRecord> getTable() {
		return dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(LoginStateRecord.class));
	}

	@Override
	public void saveState(@NonNull String sessionId, @NonNull String clientId,
			@NonNull String redirectUri, @NonNull String primaryState,
			@NonNull String primaryCodeChallenge, @NonNull String secondaryState, long ttl) {
		// Hashing the sessionId, it is sensitive data that we want to keep away from
		// even admins
		String pk = HashingUtil.sha256(sessionId);

		LoginStateRecord record = LoginStateRecord.builder()
				.pk(pk)
				.clientId(clientId)
				.redirectUri(redirectUri)
				.primaryState(primaryState)
				.primaryCodeChallenge(primaryCodeChallenge)
				.secondaryState(secondaryState)
				.ttl(ttl)
				.build();

		DynamoDbTable<LoginStateRecord> table = getTable();
		table.putItem(record);
	}

	@Override
	public LoginState getState(@NonNull String sessionId) {
		String pk = HashingUtil.sha256(sessionId);
		DynamoDbTable<LoginStateRecord> table = getTable();
		Key key = Key.builder().partitionValue(pk).build();
		return table.getItem(key);
	}

	@Override
	public void deleteState(@NonNull String sessionId) {
		String pk = HashingUtil.sha256(sessionId);
		DynamoDbTable<LoginStateRecord> table = getTable();
		Key key = Key.builder().partitionValue(pk).build();
		table.deleteItem(key);
	}
}
