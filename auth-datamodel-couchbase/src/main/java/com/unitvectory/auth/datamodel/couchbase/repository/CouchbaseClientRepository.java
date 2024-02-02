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
import com.unitvectory.auth.datamodel.model.ClientSummaryConnection;
import com.unitvectory.auth.datamodel.model.ClientSummaryEdge;
import com.unitvectory.auth.datamodel.model.ClientType;
import com.unitvectory.auth.datamodel.model.PageInfo;
import com.unitvectory.auth.datamodel.repository.ClientRepository;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@AllArgsConstructor
public class CouchbaseClientRepository implements ClientRepository {

	private final Cluster couchbaseCluster;

	private final Collection collectionClients;

	@Override
	public ClientSummaryConnection getClients(Integer first, String after, Integer last,
			String before) {
		List<ClientSummaryEdge> edges = new ArrayList<>();
		PageInfo pageInfo = null;
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("SELECT clientId, description FROM `")
				.append(this.collectionClients.bucketName()).append("`.`")
				.append(this.collectionClients.scopeName()).append("`.`")
				.append(this.collectionClients.name()).append("` ");

		// Forward Pagination
		if (first != null && after != null) {
			queryBuilder.append("WHERE clientId > '").append(after)
					.append("' ORDER BY clientId ASC LIMIT ").append(first + 1);
		} else if (first != null) {
			queryBuilder.append("ORDER BY clientId ASC LIMIT ").append(first + 1);
		}

		// Reverse Pagination
		if (last != null && before != null) {
			queryBuilder.append("WHERE clientId < '").append(before)
					.append("' ORDER BY clientId DESC LIMIT ").append(last + 1);
		} else if (last != null) {
			queryBuilder.append("ORDER BY clientId DESC LIMIT ").append(last + 1);
		}

		// Execute Query
		QueryResult result = couchbaseCluster.query(queryBuilder.toString());
		List<ClientSummaryRecord> records = result.rowsAs(ClientSummaryRecord.class);

		// Determine hasNextPage and hasPreviousPage
		boolean hasNextPage = (first != null) && records.size() > first;
		boolean hasPreviousPage = (last != null) && records.size() > last;

		if (hasNextPage || hasPreviousPage) {
			records.remove(records.size() - 1); // Remove extra record used for determining page
												// availability
		}

		// Construct edges
		records.forEach(record -> edges.add(
				ClientSummaryEdge.builder().node(record).cursor(record.getClientId()).build()));

		// Setting PageInfo
		String startCursor = edges.isEmpty() ? null : edges.get(0).getCursor();
		String endCursor = edges.isEmpty() ? null : edges.get(edges.size() - 1).getCursor();
		pageInfo = PageInfo.builder().hasNextPage(hasNextPage).hasPreviousPage(hasPreviousPage)
				.startCursor(startCursor).endCursor(endCursor).build();

		return ClientSummaryConnection.builder().edges(edges).pageInfo(pageInfo).build();
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
			@NonNull String salt, @NonNull ClientType clientType) {
		ClientRecord client = ClientRecord.builder().clientId(clientId).description(description)
				.salt(salt).clientType(clientType).build();
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
