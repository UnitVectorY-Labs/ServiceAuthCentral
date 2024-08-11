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
package com.unitvectory.serviceauthcentral.datamodel.repository;

import com.unitvectory.serviceauthcentral.datamodel.model.LoginCode;

/**
 * Interface for repository handling the OAuth 2.0 authorization code for user login.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public interface LoginCodeRepository {

	/**
	 * Saves the login code
	 * 
	 * @param code the authorization code that identifies the record
	 * @param clientId the client id for the 3rd party provider
	 * @param redirectUri the redirect URL for the web client
	 * @param codeChallenge the code challenge for PKCE for completing the user login flow
	 * @param userClientId the client id for the user that successfully authenticated
	 * @param ttl epoch time for when the authorization code expires
	 */
	void saveCode(String code, String clientId, String redirectUri, String codeChallenge,
			String userClientId, long ttl);

	/**
	 * Retrieves the authorization code information
	 * 
	 * @param code the authorization code that identifies the record
	 * @return the login code
	 */
	LoginCode getCode(String code);

	/**
	 * Deletes the authorization code information
	 * 
	 * @param code the authorization code that identifies the record
	 */
	void deleteCode(String code);
}
