package com.unitvectory.auth.datamodel.couchbase.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.unitvectory.auth.datamodel.couchbase.model.AuthorizationRecord;
import com.unitvectory.auth.datamodel.model.Authorization;
import com.unitvectory.auth.datamodel.repository.AuthorizationRepository;
import com.unitvectory.auth.util.HashingUtil;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class CouchbaseAuthorizationRepository implements AuthorizationRepository {

	private final Cluster couchbaseCluster;

	private final Collection collectionAuthorizations;

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
	public void authorize(@NonNull String subject, @NonNull String audience) {
		String docId = getDocumentId(subject, audience);
		AuthorizationRecord record =
				AuthorizationRecord.builder().subject(subject).audience(audience).build();
		this.collectionAuthorizations.insert(docId, record);
	}

	@Override
	public void deauthorize(@NonNull String subject, @NonNull String audience) {
		String docId = getDocumentId(subject, audience);
		this.collectionAuthorizations.remove(docId);
	}

	private String getDocumentId(@NonNull String subject, @NonNull String audience) {
		String subjectHash = HashingUtil.sha256(subject);
		String audienceHash = HashingUtil.sha256(audience);
		return HashingUtil.sha256(subjectHash + audienceHash);
	}
}