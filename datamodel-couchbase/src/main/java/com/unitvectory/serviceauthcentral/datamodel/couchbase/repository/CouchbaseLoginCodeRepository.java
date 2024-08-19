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
import com.unitvectory.serviceauthcentral.datamodel.couchbase.model.LoginCodeRecord;
import com.unitvectory.serviceauthcentral.datamodel.model.LoginCode;
import com.unitvectory.serviceauthcentral.datamodel.repository.LoginCodeRepository;
import com.unitvectory.serviceauthcentral.util.HashingUtil;

import lombok.AllArgsConstructor;

/**
 * The Couchbase Login Code Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
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
