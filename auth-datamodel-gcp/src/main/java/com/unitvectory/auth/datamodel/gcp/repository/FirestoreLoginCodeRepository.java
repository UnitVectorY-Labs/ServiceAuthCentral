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
import com.unitvectory.auth.datamodel.gcp.model.LoginCodeRecord;
import com.unitvectory.auth.datamodel.model.LoginCode;
import com.unitvectory.auth.datamodel.repository.LoginCodeRepository;
import com.unitvectory.auth.util.HashingUtil;
import com.unitvectory.auth.util.exception.InternalServerErrorException;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * The Firestore Login Code Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@SuppressWarnings("null")
@AllArgsConstructor
public class FirestoreLoginCodeRepository implements LoginCodeRepository {

	private Firestore firestore;

	private String collectionLoginCodes;

	@Override
	public void saveCode(@NonNull String code, @NonNull String clientId,
			@NonNull String redirectUri, @NonNull String codeChallenge,
			@NonNull String userClientId, long ttl) {
		LoginCodeRecord record = new LoginCodeRecord(clientId, redirectUri, codeChallenge,
				userClientId, Timestamp.ofTimeSecondsAndNanos(ttl, 0));

		// Hashing the code, we are not storing the code in the database directly as it
		// is sensitive data that we want to keep away from even admins
		String documentId = HashingUtil.sha256(code);

		try {
			firestore.collection(this.collectionLoginCodes).document(documentId).set(record).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public LoginCode getCode(@NonNull String code) {
		try {
			String documentId = HashingUtil.sha256(code);

			DocumentSnapshot document = firestore.collection(this.collectionLoginCodes)
					.document(documentId).get().get();
			if (document.exists()) {
				LoginCodeRecord record = document.toObject(LoginCodeRecord.class);
				return record;
			} else {
				return null;
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void deleteCode(@NonNull String code) {
		try {
			String documentId = HashingUtil.sha256(code);

			firestore.collection(this.collectionLoginCodes).document(documentId).delete().get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}
}
