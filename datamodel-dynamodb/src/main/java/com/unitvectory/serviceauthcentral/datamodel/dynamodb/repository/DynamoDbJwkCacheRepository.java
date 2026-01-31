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
import java.util.Collections;
import java.util.List;

import com.unitvectory.serviceauthcentral.datamodel.dynamodb.mapper.CachedJwkRecordMapper;
import com.unitvectory.serviceauthcentral.datamodel.dynamodb.model.CachedJwkRecord;
import com.unitvectory.serviceauthcentral.datamodel.model.CachedJwk;
import com.unitvectory.serviceauthcentral.datamodel.repository.JwkCacheRepository;
import com.unitvectory.serviceauthcentral.util.HashingUtil;

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
 * The DynamoDB JWK Cache Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@AllArgsConstructor
public class DynamoDbJwkCacheRepository implements JwkCacheRepository {

	private DynamoDbEnhancedClient dynamoDbEnhancedClient;

	private String tableName;

	private DynamoDbTable<CachedJwkRecord> getTable() {
		return dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(CachedJwkRecord.class));
	}

	private String getId(@NonNull String url, @NonNull String id) {
		String urlHash = HashingUtil.sha256(url);
		String idHash = HashingUtil.sha256(id);
		return HashingUtil.sha256(urlHash + idHash);
	}

	@Override
	public void cacheJwk(@NonNull String url, @NonNull CachedJwk jwk, long ttl) {
		CachedJwkRecord cachedJwk = CachedJwkRecordMapper.INSTANCE.cachedJwkToCachedJwkRecord(url, ttl, jwk);
		String pk = getId(url, jwk.getKid());
		cachedJwk.setPk(pk);

		DynamoDbTable<CachedJwkRecord> table = getTable();
		table.putItem(cachedJwk);
	}

	@Override
	public void cacheJwkAbsent(@NonNull String url, @NonNull String kid, long ttl) {
		String pk = getId(url, kid);

		CachedJwkRecord cachedJwk = CachedJwkRecord.builder()
				.pk(pk)
				.url(url)
				.kid(kid)
				.ttl(ttl)
				.valid(false)
				.build();

		DynamoDbTable<CachedJwkRecord> table = getTable();
		table.putItem(cachedJwk);
	}

	@Override
	public List<CachedJwk> getJwks(@NonNull String url) {
		DynamoDbTable<CachedJwkRecord> table = getTable();
		DynamoDbIndex<CachedJwkRecord> index = table.index("url-index");

		QueryConditional queryConditional = QueryConditional
				.keyEqualTo(Key.builder().partitionValue(url).build());

		QueryEnhancedRequest request = QueryEnhancedRequest.builder()
				.queryConditional(queryConditional)
				.build();

		List<CachedJwk> list = new ArrayList<>();
		for (Page<CachedJwkRecord> page : index.query(request)) {
			for (CachedJwkRecord record : page.items()) {
				list.add(record);
			}
		}

		return Collections.unmodifiableList(list);
	}

	@Override
	public CachedJwk getJwk(String url, String kid) {
		String pk = getId(url, kid);
		DynamoDbTable<CachedJwkRecord> table = getTable();
		Key key = Key.builder().partitionValue(pk).build();
		return table.getItem(key);
	}
}
