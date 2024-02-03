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

import java.util.concurrent.ExecutionException;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.unitvectory.auth.datamodel.gcp.model.LoginStateRecord;
import com.unitvectory.auth.datamodel.model.LoginState;
import com.unitvectory.auth.datamodel.repository.LoginStateRepository;
import com.unitvectory.auth.util.HashingUtil;
import com.unitvectory.auth.util.exception.InternalServerErrorException;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * The Firestore Login State Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@SuppressWarnings("null")
@AllArgsConstructor
public class FirestoreLoginStateRepository implements LoginStateRepository {

	private Firestore firestore;

	private String collectionLoginStates;

	@Override
	public void saveState(@NonNull String sessionId, @NonNull String clientId,
			@NonNull String redirectUri, @NonNull String primaryState,
			@NonNull String primaryCodeChallenge, @NonNull String secondaryState, long ttl) {
		LoginStateRecord record = new LoginStateRecord(clientId, redirectUri, primaryState,
				primaryCodeChallenge, secondaryState, Timestamp.ofTimeSecondsAndNanos(ttl, 0));

		// Hashing the sessionId, it is sensitive data that we want to keep away from
		// even admins
		String documentId = HashingUtil.sha256(sessionId);

		try {
			firestore.collection(this.collectionLoginStates).document(documentId).set(record).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public LoginState getState(@NonNull String sessionId) {
		try {
			String documentId = HashingUtil.sha256(sessionId);

			DocumentSnapshot document = firestore.collection(this.collectionLoginStates)
					.document(documentId).get().get();
			if (document.exists()) {
				LoginStateRecord record = document.toObject(LoginStateRecord.class);
				return record;
			} else {
				return null;
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void deleteState(@NonNull String sessionId) {
		try {
			String documentId = HashingUtil.sha256(sessionId);

			firestore.collection(this.collectionLoginStates).document(documentId).delete().get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}
}
