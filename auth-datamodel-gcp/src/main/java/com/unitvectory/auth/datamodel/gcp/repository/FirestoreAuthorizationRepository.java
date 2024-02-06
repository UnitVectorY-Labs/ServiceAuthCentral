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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.unitvectory.auth.common.service.time.TimeService;
import com.unitvectory.auth.datamodel.gcp.mapper.ClientScopeMapper;
import com.unitvectory.auth.datamodel.gcp.model.AuthorizationRecord;
import com.unitvectory.auth.datamodel.gcp.model.ClientRecord;
import com.unitvectory.auth.datamodel.gcp.model.ClientScopeRecord;
import com.unitvectory.auth.datamodel.model.Authorization;
import com.unitvectory.auth.datamodel.model.ClientScope;
import com.unitvectory.auth.datamodel.repository.AuthorizationRepository;
import com.unitvectory.auth.util.HashingUtil;
import com.unitvectory.auth.util.exception.BadRequestException;
import com.unitvectory.auth.util.exception.InternalServerErrorException;
import com.unitvectory.auth.util.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * The Firestore Authorization Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@SuppressWarnings("null")
@AllArgsConstructor
public class FirestoreAuthorizationRepository implements AuthorizationRepository {

	private static final String AUDIENCE = "audience";

	private static final String SUBJECT = "subject";

	private Firestore firestore;

	private String collectionAuthorizations;

	private TimeService timeService;

	@Override
	public Authorization getAuthorization(@NonNull String id) {
		try {
			DocumentSnapshot document =
					firestore.collection(this.collectionAuthorizations).document(id).get().get();
			if (document.exists()) {
				return document.toObject(AuthorizationRecord.class);
			} else {
				return null;
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void deleteAuthorization(@NonNull String id) {
		try {
			firestore.collection(this.collectionAuthorizations).document(id).delete().get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public Authorization getAuthorization(@NonNull String subject, @NonNull String audience) {
		try {
			QuerySnapshot querySnapshot = firestore.collection(this.collectionAuthorizations)
					.whereEqualTo(SUBJECT, subject).whereEqualTo(AUDIENCE, audience).limit(1).get()
					.get();

			// Process the query results
			List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
			if (!documents.isEmpty()) {
				// Convert the first document to a ClientRecord object
				return documents.get(0).toObject(AuthorizationRecord.class);
			} else {
				return null;
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public Iterator<Authorization> getAuthorizationBySubject(@NonNull String subject) {
		try {
			QuerySnapshot querySnapshot = firestore.collection(this.collectionAuthorizations)
					.whereEqualTo(SUBJECT, subject).get().get();

			ArrayList<Authorization> list = new ArrayList<>();

			List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
			for (QueryDocumentSnapshot document : documents) {
				list.add(document.toObject(AuthorizationRecord.class));
			}

			return list.iterator();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public Iterator<Authorization> getAuthorizationByAudience(@NonNull String audience) {
		try {
			QuerySnapshot querySnapshot = firestore.collection(this.collectionAuthorizations)
					.whereEqualTo(AUDIENCE, audience).get().get();

			ArrayList<Authorization> list = new ArrayList<>();

			List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
			for (QueryDocumentSnapshot document : documents) {
				list.add(document.toObject(AuthorizationRecord.class));
			}

			return list.iterator();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void authorize(@NonNull String subject, @NonNull String audience,
			@NonNull List<String> authorizedScopes) {
		AuthorizationRecord record = AuthorizationRecord.builder()
				.authorizationCreated(this.timeService.getCurrentTimestamp()).subject(subject)
				.audience(audience).authorizedScopes(authorizedScopes).build();
		String documentId = getDocumentId(subject, audience);
		DocumentReference docRef =
				this.firestore.collection(this.collectionAuthorizations).document(documentId);
		docRef.set(record);
	}

	@Override
	public void deauthorize(@NonNull String subject, @NonNull String audience) {
		String documentId = getDocumentId(subject, audience);
		DocumentReference docRef =
				this.firestore.collection(this.collectionAuthorizations).document(documentId);
		docRef.delete();
	}

	@Override
	public void authorizeAddScope(@NonNull String subject, @NonNull String audience,
			@NonNull String authorizedScope) {
		String documentId = getDocumentId(subject, audience);

		try {
			DocumentReference docRef =
					firestore.collection(this.collectionAuthorizations).document(documentId);
			firestore.runTransaction(transaction -> {
				DocumentSnapshot snapshot = transaction.get(docRef).get();

				if (!snapshot.exists()) {
					throw new NotFoundException("Client not found");
				}

				AuthorizationRecord record = snapshot.toObject(AuthorizationRecord.class);

				if (record.getAuthorizedScopes().contains(authorizedScope)) {
					throw new BadRequestException("Scope already authorized");
				}

				List<String> authorizedScopesList = new ArrayList<>();
				if (record.getAuthorizedScopes() != null) {
					authorizedScopesList.addAll(record.getAuthorizedScopes());
				}

				authorizedScopesList.add(authorizedScope);
				transaction.update(docRef, "authorizedScopes", authorizedScopesList);

				return null; // Firestore transactions require that you return something
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void authorizeRemoveScope(@NonNull String subject, @NonNull String audience,
			@NonNull String authorizedScope) {
		String documentId = getDocumentId(subject, audience);

		try {
			DocumentReference docRef =
					firestore.collection(this.collectionAuthorizations).document(documentId);
			firestore.runTransaction(transaction -> {
				DocumentSnapshot snapshot = transaction.get(docRef).get();

				if (!snapshot.exists()) {
					throw new NotFoundException("Client not found");
				}

				AuthorizationRecord record = snapshot.toObject(AuthorizationRecord.class);

				List<String> authorizedScopesList = new ArrayList<>();
				if (record.getAuthorizedScopes() != null) {
					authorizedScopesList.addAll(record.getAuthorizedScopes());
				}

				authorizedScopesList.remove(authorizedScope);
				transaction.update(docRef, "authorizedScopes", authorizedScopesList);

				return null; // Firestore transactions require that you return something
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	private String getDocumentId(@NonNull String subject, @NonNull String audience) {
		String subjectHash = HashingUtil.sha256(subject);
		String audienceHash = HashingUtil.sha256(audience);
		return HashingUtil.sha256(subjectHash + audienceHash);
	}
}
