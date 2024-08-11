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
package com.unitvectory.serviceauthcentral.datamodel.couchbase.repository;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Collection;
import com.unitvectory.serviceauthcentral.datamodel.couchbase.model.LoginStateRecord;
import com.unitvectory.serviceauthcentral.datamodel.model.LoginState;
import com.unitvectory.serviceauthcentral.datamodel.repository.LoginStateRepository;
import com.unitvectory.serviceauthcentral.util.HashingUtil;

import lombok.AllArgsConstructor;

/**
 * The Couchbase Login State Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@AllArgsConstructor
public class CouchbaseLoginStateRepository implements LoginStateRepository {

	private final Collection collectionLoginState;

	@Override
	public void saveState(String sessionId, String clientId, String redirectUri,
			String primaryState, String primaryCodeChallenge, String secondaryState, long ttl) {
		String documentId = HashingUtil.sha256(sessionId);

		LoginStateRecord loginState =
				LoginStateRecord.builder().clientId(clientId).redirectUri(redirectUri)
						.primaryState(primaryState).primaryCodeChallenge(primaryCodeChallenge)
						.secondaryState(secondaryState).ttl(ttl).build();
		this.collectionLoginState.insert(documentId, loginState);
	}

	@Override
	public LoginState getState(String sessionId) {
		String documentId = HashingUtil.sha256(sessionId);
		try {
			return this.collectionLoginState.get(documentId).contentAs(LoginStateRecord.class);
		} catch (DocumentNotFoundException e) {
			return null;
		}
	}

	@Override
	public void deleteState(String sessionId) {
		String documentId = HashingUtil.sha256(sessionId);
		this.collectionLoginState.remove(documentId);
	}
}
