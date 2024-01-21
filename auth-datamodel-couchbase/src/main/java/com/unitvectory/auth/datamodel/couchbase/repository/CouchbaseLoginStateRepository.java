package com.unitvectory.auth.datamodel.couchbase.repository;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Collection;
import com.unitvectory.auth.datamodel.couchbase.model.LoginStateRecord;
import com.unitvectory.auth.datamodel.model.LoginState;
import com.unitvectory.auth.datamodel.repository.LoginStateRepository;
import com.unitvectory.auth.util.HashingUtil;

import lombok.AllArgsConstructor;

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
