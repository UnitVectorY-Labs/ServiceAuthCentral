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
package com.unitvectory.auth.datamodel.couchbase.repository;

import java.util.ArrayList;
import java.util.List;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.unitvectory.auth.datamodel.couchbase.mapper.CachedJwkRecordMapper;
import com.unitvectory.auth.datamodel.couchbase.model.CachedJwkRecord;
import com.unitvectory.auth.datamodel.model.CachedJwk;
import com.unitvectory.auth.datamodel.repository.JwkCacheRepository;
import com.unitvectory.auth.util.HashingUtil;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * The Couchbase JWK Cache Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@AllArgsConstructor
public class CouchbaseJwkCacheRepository implements JwkCacheRepository {

	private final Cluster couchbaseCluster;

	private final Collection collectionKeys;

	private String getId(@NonNull String url, @NonNull String id) {
		String urlHash = HashingUtil.sha256(url);
		String idHash = HashingUtil.sha256(id);
		return HashingUtil.sha256(urlHash + idHash);
	}

	@Override
	public void cacheJwk(@NonNull String url, @NonNull CachedJwk jwk, long ttl) {
		CachedJwkRecord record =
				CachedJwkRecordMapper.INSTANCE.cachedJwkToCachedJwkRecord(url, ttl, jwk);
		String documentId = getId(url, jwk.getKid());
		collectionKeys.insert(documentId, record);
	}

	@Override
	public void cacheJwkAbsent(@NonNull String url, @NonNull String kid, long ttl) {
		String documentId = getId(url, kid);
		CachedJwkRecord cachedJwk =
				CachedJwkRecord.builder().documentId(documentId).url(url).kid(kid).ttl(ttl).build();
		collectionKeys.insert(documentId, cachedJwk);
	}

	@Override
	public List<CachedJwk> getJwks(@NonNull String url) {
		List<CachedJwk> jwks = new ArrayList<>();
		final String query =
				"SELECT meta().id as documentId, url, ttl, valid, kid, kty, alg, `use`, n, e "
						+ "FROM `" + this.collectionKeys.bucketName() + "`.`"
						+ this.collectionKeys.scopeName() + "`.`" + this.collectionKeys.name()
						+ "` " + "WHERE url = $url";

		JsonObject queryParams = JsonObject.create().put("url", url);
		QueryResult result =
				couchbaseCluster.query(query, QueryOptions.queryOptions().parameters(queryParams));

		result.rowsAs(CachedJwkRecord.class).forEach(row -> {
			jwks.add(row);
		});

		return jwks;
	}

	@Override
	public CachedJwk getJwk(@NonNull String url, @NonNull String kid) {
		String documentId = getId(url, kid);
		try {
			return this.collectionKeys.get(documentId).contentAs(CachedJwkRecord.class);
		} catch (DocumentNotFoundException e) {
			return null;
		}
	}
}
