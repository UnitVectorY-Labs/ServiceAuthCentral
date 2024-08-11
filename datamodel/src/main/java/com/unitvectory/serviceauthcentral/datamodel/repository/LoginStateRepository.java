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

import com.unitvectory.serviceauthcentral.datamodel.model.LoginState;

/**
 * Interface for repository handling the OAuth 2.0 authorization code.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public interface LoginStateRepository {

	/**
	 * Saves the login state record.
	 * 
	 * @param sessionId the session identifier
	 * @param clientId the client id of the provider
	 * @param redirectUri the redirect url for the requesting client
	 * @param primaryState the state parameter for the requesting client
	 * @param primaryCodeChallenge the code challenge for PKCE for the requesting client
	 * @param secondaryState the state parameter for completing the flow for the provider
	 * @param ttl epoch time for when the session expires
	 */
	void saveState(String sessionId, String clientId, String redirectUri, String primaryState,
			String primaryCodeChallenge, String secondaryState, long ttl);

	/**
	 * Gets the state record.
	 * 
	 * @param sessionId the session identifier
	 * @return the login state record
	 */
	LoginState getState(String sessionId);

	/**
	 * Deletes the state records.
	 * 
	 * @param sessionId the session identifier
	 */
	void deleteState(String sessionId);

}
