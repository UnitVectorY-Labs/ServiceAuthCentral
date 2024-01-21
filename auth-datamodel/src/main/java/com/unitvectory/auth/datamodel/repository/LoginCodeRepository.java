package com.unitvectory.auth.datamodel.repository;

import com.unitvectory.auth.datamodel.model.LoginCode;

/**
 * Interface for repository handling the OAuth 2.0 authorization code for user login.
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
