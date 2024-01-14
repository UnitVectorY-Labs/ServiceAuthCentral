package com.unitvectory.auth.datamodel.couchbase.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.MutateInSpec;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.unitvectory.auth.datamodel.couchbase.model.ClientRecord;
import com.unitvectory.auth.datamodel.couchbase.model.ClientSummaryRecord;
import com.unitvectory.auth.datamodel.model.Client;
import com.unitvectory.auth.datamodel.model.ClientSummary;
import com.unitvectory.auth.datamodel.repository.ClientRepository;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class CouchbaseClientRepository implements ClientRepository {

	private final Cluster couchbaseCluster;

	private final Collection collectionClients;

	@Override
	public List<ClientSummary> getClients() {
		List<ClientSummary> records = new ArrayList<>();
		final String query = "SELECT clientId, description " + "FROM `"
				+ this.collectionClients.bucketName() + "`.`" + this.collectionClients.scopeName()
				+ "`.`" + this.collectionClients.name() + "` ";

		QueryResult result = couchbaseCluster.query(query);

		result.rowsAs(ClientSummaryRecord.class).forEach(row -> {
			records.add(row);
		});

		return records;
	}

	@Override
	public Client getClient(@NonNull String clientId) {
		try {
			return this.collectionClients.get(clientId).contentAs(ClientRecord.class);
		} catch (DocumentNotFoundException e) {
			return null;
		}
	}

	@Override
	public void deleteClient(@NonNull String clientId) {
		this.collectionClients.remove(clientId);
	}

	@Override
	public void putClient(@NonNull String clientId, @NonNull String description,
			@NonNull String salt) {
		ClientRecord client = ClientRecord.builder().clientId(clientId).description(description)
				.salt(salt).build();
		this.collectionClients.insert(clientId, client);
	}

	@Override
	public void addAuthorizedJwt(@NonNull String clientId, @NonNull String id,
			@NonNull String jwksUrl, @NonNull String iss, @NonNull String sub,
			@NonNull String aud) {

		String query = "UPDATE `" + this.collectionClients.bucketName() + "`.`"
				+ this.collectionClients.scopeName() + "`.`" + this.collectionClients.name() + "` "
				+ "SET jwtBearer = ARRAY_APPEND(jwtBearer, {"
				+ "\"aud\": $aud, \"id\": $id, \"iss\": $iss, "
				+ "\"jwksUrl\": $jwksUrl, \"sub\": $sub}) " + "WHERE clientId = $clientId";

		JsonObject parameters = JsonObject.create().put("aud", aud).put("id", id).put("iss", iss)
				.put("jwksUrl", jwksUrl).put("sub", sub).put("clientId", clientId);

		couchbaseCluster.query(query, QueryOptions.queryOptions().parameters(parameters));
	}

	@Override
	public void removeAuthorizedJwt(@NonNull String clientId, @NonNull String id) {

		String query = "UPDATE `" + this.collectionClients.bucketName() + "`.`"
				+ this.collectionClients.scopeName() + "`.`" + this.collectionClients.name() + "` "
				+ "SET jwtBearer = ARRAY v FOR v IN jwtBearer WHEN v.id != $id END "
				+ "WHERE clientId = $clientId AND ANY v IN jwtBearer SATISFIES v.id = $id END";

		JsonObject parameters = JsonObject.create().put("id", id).put("clientId", clientId);

		couchbaseCluster.query(query, QueryOptions.queryOptions().parameters(parameters));
	}

	@Override
	public void saveClientSecret1(@NonNull String clientId, @NonNull String hashedSecret) {
		this.collectionClients.mutateIn(clientId,
				Collections.singletonList(MutateInSpec.upsert("clientSecret1", hashedSecret)));
	}

	@Override
	public void saveClientSecret2(@NonNull String clientId, @NonNull String hashedSecret) {
		this.collectionClients.mutateIn(clientId,
				Collections.singletonList(MutateInSpec.upsert("clientSecret2", hashedSecret)));
	}

	@Override
	public void clearClientSecret1(@NonNull String clientId) {
		this.collectionClients.mutateIn(clientId,
				Collections.singletonList(MutateInSpec.upsert("clientSecret1", null)));
	}

	@Override
	public void clearClientSecret2(@NonNull String clientId) {
		this.collectionClients.mutateIn(clientId,
				Collections.singletonList(MutateInSpec.upsert("clientSecret2", null)));
	}

}
