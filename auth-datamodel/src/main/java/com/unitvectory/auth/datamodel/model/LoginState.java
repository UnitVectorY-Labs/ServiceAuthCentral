package com.unitvectory.auth.datamodel.model;

/**
 * Interface representing the login state during the authorization code loigin flow that supports
 * PKCE.
 */
public interface LoginState {

	/**
	 * Returns the client id
	 * 
	 * @return the client id
	 */
	String getClientId();

	/**
	 * Returns the redirect URI of the client used by the initiating client
	 * 
	 * @return the redirect URI
	 */
	String getRedirectUri();

	/**
	 * Returns the primary state parameter; the one used by the initiating client
	 * 
	 * @return the primary state
	 */
	String getPrimaryState();

	/**
	 * Returns the primary code challenge parameter; this is used to validate PKCE; the one used by
	 * the initiating client
	 * 
	 * @return the primary code challenge
	 */
	String getPrimaryCodeChallenge();

	/**
	 * Returns the secondary state; the one used to validate the 3rd party OAuth flow.
	 * 
	 * @return the secondary state
	 */
	String getSecondaryState();
}
