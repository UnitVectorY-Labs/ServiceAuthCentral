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
import java.util.Iterator;
import java.util.List;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.unitvectory.auth.common.service.time.TimeService;
import com.unitvectory.auth.datamodel.couchbase.model.AuthorizationRecord;
import com.unitvectory.auth.datamodel.model.Authorization;
import com.unitvectory.auth.datamodel.repository.AuthorizationRepository;
import com.unitvectory.auth.util.HashingUtil;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * The Couchbase Authorization Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@AllArgsConstructor
public class CouchbaseAuthorizationRepository implements AuthorizationRepository {

	private final Cluster couchbaseCluster;

	private final Collection collectionAuthorizations;

	private final TimeService timeService;

	@Override
	public Authorization getAuthorization(@NonNull String id) {
		return this.collectionAuthorizations.get(id).contentAs(AuthorizationRecord.class);
	}

	@Override
	public void deleteAuthorization(@NonNull String id) {
		this.collectionAuthorizations.remove(id);
	}

	@Override
	public Authorization getAuthorization(@NonNull String subject, @NonNull String audience) {
		String docId = getDocumentId(subject, audience);
		return this.collectionAuthorizations.get(docId).contentAs(AuthorizationRecord.class);
	}

	@Override
	public Iterator<Authorization> getAuthorizationBySubject(@NonNull String subject) {
		List<Authorization> records = new ArrayList<>();
		final String query = "SELECT meta().id as documentId, subject, audience " + "FROM `"
				+ this.collectionAuthorizations.bucketName() + "`.`"
				+ this.collectionAuthorizations.scopeName() + "`.`"
				+ this.collectionAuthorizations.name() + "` " + "WHERE subject = $subject";

		JsonObject queryParams = JsonObject.create().put("subject", subject);
		QueryResult result =
				couchbaseCluster.query(query, QueryOptions.queryOptions().parameters(queryParams));

		result.rowsAs(AuthorizationRecord.class).forEach(row -> {
			records.add(row);
		});

		return records.iterator();
	}

	@Override
	public Iterator<Authorization> getAuthorizationByAudience(@NonNull String audience) {
		List<Authorization> records = new ArrayList<>();
		final String query = "SELECT meta().id as documentId, subject, audience " + "FROM `"
				+ this.collectionAuthorizations.bucketName() + "`.`"
				+ this.collectionAuthorizations.scopeName() + "`.`"
				+ this.collectionAuthorizations.name() + "` " + "WHERE audience = $audience";

		JsonObject queryParams = JsonObject.create().put("audience", audience);
		QueryResult result =
				couchbaseCluster.query(query, QueryOptions.queryOptions().parameters(queryParams));

		result.rowsAs(AuthorizationRecord.class).forEach(row -> {
			records.add(row);
		});

		return records.iterator();
	}

	@Override
	public void authorize(@NonNull String subject, @NonNull String audience,
			@NonNull List<String> authorizedScopes) {
		String docId = getDocumentId(subject, audience);
		AuthorizationRecord record = AuthorizationRecord.builder()
				.authorizationCreated(this.timeService.getCurrentTimestamp()).subject(subject)
				.audience(audience).authorizedScopes(authorizedScopes).build();
		this.collectionAuthorizations.insert(docId, record);
	}

	@Override
	public void deauthorize(@NonNull String subject, @NonNull String audience) {
		String docId = getDocumentId(subject, audience);
		this.collectionAuthorizations.remove(docId);
	}

	@Override
	public void authorizeAddScope(@NonNull String subject, @NonNull String audience,
			@NonNull String authorizedScope) {
		String docId = getDocumentId(subject, audience);
		String query = "UPDATE `" + this.collectionAuthorizations.bucketName() + "`.`"
				+ this.collectionAuthorizations.scopeName() + "`.`"
				+ this.collectionAuthorizations.name() + "` "
				+ "SET authorizedScopes = ARRAY_APPEND(IFMISSINGORNULL(authorizedScopes, []), $authorizedScope) "
				+ "WHERE meta().id = $docId";

		JsonObject parameters =
				JsonObject.create().put("authorizedScope", authorizedScope).put("docId", docId);

		couchbaseCluster.query(query, QueryOptions.queryOptions().parameters(parameters));
	}


	@Override
	public void authorizeRemoveScope(@NonNull String subject, @NonNull String audience,
			@NonNull String authorizedScope) {
		String docId = getDocumentId(subject, audience);
		String query = "UPDATE `" + this.collectionAuthorizations.bucketName() + "`.`"
				+ this.collectionAuthorizations.scopeName() + "`.`"
				+ this.collectionAuthorizations.name() + "` "
				+ "SET authorizedScopes = ARRAY v FOR v IN authorizedScopes WHEN v != $authorizedScope END "
				+ "WHERE meta().id = $docId";

		JsonObject parameters =
				JsonObject.create().put("authorizedScope", authorizedScope).put("docId", docId);

		couchbaseCluster.query(query, QueryOptions.queryOptions().parameters(parameters));
	}


	private String getDocumentId(@NonNull String subject, @NonNull String audience) {
		String subjectHash = HashingUtil.sha256(subject);
		String audienceHash = HashingUtil.sha256(audience);
		return HashingUtil.sha256(subjectHash + audienceHash);
	}
}
