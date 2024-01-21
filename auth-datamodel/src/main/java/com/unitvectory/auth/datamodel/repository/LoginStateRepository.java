package com.unitvectory.auth.datamodel.repository;

import com.unitvectory.auth.datamodel.model.LoginState;

/**
 * Interface for repository handling the OAuth 2.0 authorization code.
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
