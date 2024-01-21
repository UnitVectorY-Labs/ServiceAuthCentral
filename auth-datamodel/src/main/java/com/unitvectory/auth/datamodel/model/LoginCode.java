package com.unitvectory.auth.datamodel.model;

/**
 * Interface representing the OAuth authorization code record.
 */
public interface LoginCode {

	/**
	 * Returns the client id
	 * 
	 * @return the client id
	 */
	String getClientId();

	/**
	 * Returns the redirect URI
	 * 
	 * @return the redirect URI
	 */
	String getRedirectUri();

	/**
	 * Returns the code challenge for PKCE
	 * 
	 * @return the code challenge
	 */
	String getCodeChallenge();

	/**
	 * Returns the user client id
	 * 
	 * This is in the format "user:{provider}:{userId}"
	 * 
	 * @return the user client id
	 */
	String getUserClientId();

}
