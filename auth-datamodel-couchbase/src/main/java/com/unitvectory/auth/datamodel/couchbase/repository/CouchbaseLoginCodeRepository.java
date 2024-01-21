package com.unitvectory.auth.datamodel.couchbase.repository;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Collection;
import com.unitvectory.auth.datamodel.couchbase.model.LoginCodeRecord;
import com.unitvectory.auth.datamodel.model.LoginCode;
import com.unitvectory.auth.datamodel.repository.LoginCodeRepository;
import com.unitvectory.auth.util.HashingUtil;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CouchbaseLoginCodeRepository implements LoginCodeRepository {

	private final Collection collectionLoginCode;

	@Override
	public void saveCode(String code, String clientId, String redirectUri, String codeChallenge,
			String userClientId, long ttl) {
		String documentId = HashingUtil.sha256(code);
		LoginCodeRecord loginCode =
				LoginCodeRecord.builder().clientId(clientId).redirectUri(redirectUri)
						.codeChallenge(codeChallenge).userClientId(userClientId).ttl(ttl).build();
		this.collectionLoginCode.insert(documentId, loginCode);
	}

	@Override
	public LoginCode getCode(String code) {
		String documentId = HashingUtil.sha256(code);
		try {
			return this.collectionLoginCode.get(documentId).contentAs(LoginCodeRecord.class);
		} catch (DocumentNotFoundException e) {
			return null;
		}
	}

	@Override
	public void deleteCode(String code) {

		String documentId = HashingUtil.sha256(code);
		this.collectionLoginCode.remove(documentId);
	}

}
