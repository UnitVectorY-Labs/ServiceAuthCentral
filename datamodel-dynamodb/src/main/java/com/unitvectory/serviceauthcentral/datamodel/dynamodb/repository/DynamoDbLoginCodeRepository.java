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

import com.unitvectory.serviceauthcentral.datamodel.dynamodb.model.LoginCodeRecord;
import com.unitvectory.serviceauthcentral.datamodel.model.LoginCode;
import com.unitvectory.serviceauthcentral.datamodel.repository.LoginCodeRepository;
import com.unitvectory.serviceauthcentral.util.HashingUtil;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

/**
 * The DynamoDB Login Code Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@AllArgsConstructor
public class DynamoDbLoginCodeRepository implements LoginCodeRepository {

	private DynamoDbEnhancedClient dynamoDbEnhancedClient;

	private String tableName;

	private DynamoDbTable<LoginCodeRecord> getTable() {
		return dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(LoginCodeRecord.class));
	}

	@Override
	public void saveCode(@NonNull String code, @NonNull String clientId,
			@NonNull String redirectUri, @NonNull String codeChallenge,
			@NonNull String userClientId, long ttl) {
		// Hashing the code, we are not storing the code in the database directly as it
		// is sensitive data that we want to keep away from even admins
		String pk = HashingUtil.sha256(code);

		LoginCodeRecord record = LoginCodeRecord.builder()
				.pk(pk)
				.clientId(clientId)
				.redirectUri(redirectUri)
				.codeChallenge(codeChallenge)
				.userClientId(userClientId)
				.ttl(ttl)
				.build();

		DynamoDbTable<LoginCodeRecord> table = getTable();
		table.putItem(record);
	}

	@Override
	public LoginCode getCode(@NonNull String code) {
		String pk = HashingUtil.sha256(code);
		DynamoDbTable<LoginCodeRecord> table = getTable();
		Key key = Key.builder().partitionValue(pk).build();
		return table.getItem(key);
	}

	@Override
	public void deleteCode(@NonNull String code) {
		String pk = HashingUtil.sha256(code);
		DynamoDbTable<LoginCodeRecord> table = getTable();
		Key key = Key.builder().partitionValue(pk).build();
		table.deleteItem(key);
	}
}
