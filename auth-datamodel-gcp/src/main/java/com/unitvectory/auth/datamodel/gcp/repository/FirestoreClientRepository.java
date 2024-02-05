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
package com.unitvectory.auth.datamodel.gcp.repository;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.unitvectory.auth.common.service.time.TimeService;
import com.unitvectory.auth.datamodel.gcp.model.ClientJwtBearerRecord;
import com.unitvectory.auth.datamodel.gcp.model.ClientRecord;
import com.unitvectory.auth.datamodel.gcp.model.ClientSummaryRecord;
import com.unitvectory.auth.datamodel.model.Client;
import com.unitvectory.auth.datamodel.model.ClientJwtBearer;
import com.unitvectory.auth.datamodel.model.ClientSummary;
import com.unitvectory.auth.datamodel.model.ClientSummaryConnection;
import com.unitvectory.auth.datamodel.model.ClientSummaryEdge;
import com.unitvectory.auth.datamodel.model.ClientType;
import com.unitvectory.auth.datamodel.model.PageInfo;
import com.unitvectory.auth.datamodel.repository.ClientRepository;
import com.unitvectory.auth.util.HashingUtil;
import com.unitvectory.auth.util.exception.BadRequestException;
import com.unitvectory.auth.util.exception.ConflictException;
import com.unitvectory.auth.util.exception.InternalServerErrorException;
import com.unitvectory.auth.util.exception.NotFoundException;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * The Firestore Client Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@SuppressWarnings("null")
@AllArgsConstructor
public class FirestoreClientRepository implements ClientRepository {

	private static final String CLIENTSECRET1 = "clientSecret1";

	private static final String CLIENTSECRET2 = "clientSecret2";

	private Firestore firestore;

	private String collectionClients;

	private TimeService timeService;

	@Override
	public ClientSummaryConnection getClients(Integer first, String after, Integer last,
			String before) {
		try {
			Query query =
					firestore.collection(this.collectionClients).select("clientId", "description");
			boolean hasPreviousPage = false, hasNextPage = false;

			// Forward pagination logic
			if (first != null) {
				query = query.orderBy("clientId");
				if (after != null && !after.isEmpty()) {
					String afterDecoded =
							new String(Base64.getDecoder().decode(after), StandardCharsets.UTF_8);
					query = query.startAfter(afterDecoded);
					hasPreviousPage = checkForPreviousPage(afterDecoded);
				}
				query = query.limit(first + 1);
			}

			// Backward pagination logic
			if (last != null) {
				query = query.orderBy("clientId", Query.Direction.DESCENDING);
				if (before != null && !before.isEmpty()) {
					String beforeDecoded =
							new String(Base64.getDecoder().decode(before), StandardCharsets.UTF_8);
					query = query.startAfter(beforeDecoded); // Corrected method
					hasNextPage = checkForNextPage(beforeDecoded);
				}
				query = query.limit(last + 1);
			}

			List<ClientSummaryEdge> edges = processQueryResults(query);

			// Adjust the list for backward pagination
			if (last != null) {
				edges = reverseEdges(edges);
				hasPreviousPage = edges.size() > last;
				if (hasPreviousPage) {
					edges.remove(0); // Remove the extra item at the beginning
				}
			}

			// Adjust hasNextPage flag for forward pagination
			if (first != null && edges.size() > first) {
				hasNextPage = true;
				edges.remove(edges.size() - 1); // Remove the extra item at the end
			}

			PageInfo pageInfo = constructPageInfo(edges, hasPreviousPage, hasNextPage);
			return ClientSummaryConnection.builder().edges(edges).pageInfo(pageInfo).build();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	private boolean checkForPreviousPage(String afterDecoded)
			throws InterruptedException, ExecutionException {
		Query prevPageQuery = firestore.collection(this.collectionClients).orderBy("clientId")
				.endBefore(afterDecoded).limit(1);
		return !prevPageQuery.get().get().isEmpty();
	}

	private boolean checkForNextPage(String beforeDecoded)
			throws InterruptedException, ExecutionException {
		Query nextPageQuery = firestore.collection(this.collectionClients)
				.orderBy("clientId", Query.Direction.DESCENDING).startAfter(beforeDecoded).limit(1);
		return !nextPageQuery.get().get().isEmpty();
	}

	private List<ClientSummaryEdge> processQueryResults(Query query)
			throws InterruptedException, ExecutionException {
		QuerySnapshot querySnapshot = query.get().get();
		List<ClientSummaryEdge> edges = new ArrayList<>();
		for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
			ClientSummary summary = document.toObject(ClientSummaryRecord.class);
			String cursor = Base64.getEncoder().encodeToString(
					document.getString("clientId").getBytes(StandardCharsets.UTF_8));
			edges.add(ClientSummaryEdge.builder().node(summary).cursor(cursor).build());
		}
		return edges;
	}

	private List<ClientSummaryEdge> reverseEdges(List<ClientSummaryEdge> edges) {
		List<ClientSummaryEdge> reversed = new ArrayList<>(edges);
		Collections.reverse(reversed);
		return reversed;
	}

	private PageInfo constructPageInfo(List<ClientSummaryEdge> edges, boolean hasPreviousPage,
			boolean hasNextPage) {
		String startCursor = null, endCursor = null;
		if (!edges.isEmpty()) {
			startCursor = edges.get(0).getCursor();
			endCursor = edges.get(edges.size() - 1).getCursor();
		}
		return PageInfo.builder().hasNextPage(hasNextPage).hasPreviousPage(hasPreviousPage)
				.startCursor(startCursor).endCursor(endCursor).build();
	}

	@Override
	public Client getClient(@NonNull String clientId) {

		String documentId = HashingUtil.sha256(clientId);

		try {
			DocumentSnapshot document =
					firestore.collection(this.collectionClients).document(documentId).get().get();
			if (document.exists()) {
				ClientRecord record = document.toObject(ClientRecord.class);
				return record;
			} else {
				return null;
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void deleteClient(@NonNull String clientId) {

		String documentId = HashingUtil.sha256(clientId);

		try {
			this.firestore.collection(this.collectionClients).document(documentId).delete().get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void putClient(@NonNull String clientId, String description, @NonNull String salt,
			@NonNull ClientType clientType) {

		String documentId = HashingUtil.sha256(clientId);

		try {
			// Reference to the document in the 'clients' collection with the specified
			// clientId
			DocumentReference document =
					firestore.collection(this.collectionClients).document(documentId);

			// Start a Firestore transaction

			firestore.runTransaction(transaction -> {
				// Attempt to retrieve the existing document
				DocumentSnapshot snapshot = transaction.get(document).get();

				// If the document does not exist, create the new ClientRecord
				if (!snapshot.exists()) {
					@Nonnull
					ClientRecord record = ClientRecord.builder().documentId(documentId)
							.clientCreated(this.timeService.getCurrentTimestamp())
							.clientId(clientId).description(description).salt(salt)
							.clientType(clientType).build();

					// Perform the transactional write to create the new record
					transaction.set(document, record);
				} else {
					// Handle the case where the record already exists
					// For example, log a message or throw a custom exception
					throw new ConflictException("clientId already exists");
				}

				// Must return a result; here, null signifies nothing further to return
				return null;
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void addAuthorizedJwt(@NonNull String clientId, @NonNull String id,
			@NonNull String jwksUrl, @NonNull String iss, @NonNull String sub,
			@NonNull String aud) {

		String documentId = HashingUtil.sha256(clientId);

		try {
			DocumentReference docRef =
					firestore.collection(this.collectionClients).document(documentId);
			firestore.runTransaction(transaction -> {
				DocumentSnapshot snapshot = transaction.get(docRef).get();

				if (!snapshot.exists()) {
					throw new NotFoundException("Client not found");
				}

				ClientRecord record = snapshot.toObject(ClientRecord.class);
				List<ClientJwtBearer> jwtBearerListOriginal = record.getJwtBearer();
				List<ClientJwtBearer> jwtBearerList = new ArrayList<>();

				// If the original list is not null, add all its elements to the new list
				if (jwtBearerListOriginal != null) {
					jwtBearerList.addAll(jwtBearerListOriginal);
				}

				// Constructing new JWT object
				ClientJwtBearerRecord newJwt = ClientJwtBearerRecord.builder().id(id)
						.jwksUrl(jwksUrl).iss(iss).sub(sub).aud(aud).build();

				// Check for duplicates
				for (ClientJwtBearer cjb : jwtBearerList) {
					if (newJwt.matches(cjb)) {
						throw new BadRequestException("Duplicate authorization");
					}
				}

				// Add the new JWT to the list and update the document
				jwtBearerList.add(newJwt);
				transaction.update(docRef, "jwtBearer", jwtBearerList);

				return null; // Firestore transactions require that you return something
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void removeAuthorizedJwt(@NonNull String clientId, @NonNull String id) {

		String documentId = HashingUtil.sha256(clientId);

		try {
			DocumentReference docRef =
					firestore.collection(this.collectionClients).document(documentId);
			firestore.runTransaction(transaction -> {
				DocumentSnapshot snapshot = transaction.get(docRef).get();

				if (!snapshot.exists()) {
					throw new NotFoundException("Client not found");
				}

				ClientRecord record = snapshot.toObject(ClientRecord.class);

				List<ClientJwtBearer> jwtBearerListOriginal = record.getJwtBearer();
				List<ClientJwtBearer> jwtBearerList = new ArrayList<>();

				if (jwtBearerListOriginal != null) {
					// Copy existing JWTs into a modifiable list
					jwtBearerList.addAll(jwtBearerListOriginal);

					// Find the JWT to remove
					ClientJwtBearer jwtToRemove = null;
					for (ClientJwtBearer cjb : jwtBearerList) {
						if (id.equals(cjb.getId())) {
							jwtToRemove = cjb;
							break;
						}
					}

					// Remove the JWT if found
					if (jwtToRemove != null) {
						jwtBearerList.remove(jwtToRemove);
						// Update the document
						transaction.update(docRef, "jwtBearer", jwtBearerList);
					} else {
						throw new NotFoundException("JWT not found with the provided id");
					}
				}

				return null; // Firestore transactions require that you return something
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void saveClientSecret1(@NonNull String clientId, @NonNull String hashedSecret) {

		String documentId = HashingUtil.sha256(clientId);

		Map<String, Object> updates = new HashMap<>();
		updates.put(CLIENTSECRET1, hashedSecret);
		updates.put("clientSecret1Updated", this.timeService.getCurrentTimestamp());

		try {
			firestore.collection(this.collectionClients).document(documentId).update(updates).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void saveClientSecret2(@NonNull String clientId, @NonNull String hashedSecret) {

		String documentId = HashingUtil.sha256(clientId);

		Map<String, Object> updates = new HashMap<>();
		updates.put(CLIENTSECRET2, hashedSecret);
		updates.put("clientSecret2Updated", this.timeService.getCurrentTimestamp());

		try {
			firestore.collection(this.collectionClients).document(documentId).update(updates).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void clearClientSecret1(@NonNull String clientId) {

		String documentId = HashingUtil.sha256(clientId);

		Map<String, Object> updates = new HashMap<>();
		updates.put(CLIENTSECRET1, null);
		updates.put("clientSecret1Updated", this.timeService.getCurrentTimestamp());

		try {
			firestore.collection(this.collectionClients).document(documentId).update(updates).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void clearClientSecret2(@NonNull String clientId) {

		String documentId = HashingUtil.sha256(clientId);

		Map<String, Object> updates = new HashMap<>();
		updates.put(CLIENTSECRET2, null);
		updates.put("clientSecret2Updated", this.timeService.getCurrentTimestamp());

		try {
			firestore.collection(this.collectionClients).document(documentId).update(updates).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}
}
