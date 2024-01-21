package com.unitvectory.auth.server.token.service.provider;

import com.unitvectory.auth.server.token.model.UserContext;

/**
 * Interface to provide separate implementations for OAuth login provider.
 */
public interface LoginProviderService {

	/**
	 * True if this service is configured to be active, otherwise false.
	 * 
	 * @return the service
	 */
	boolean isActive();

	/**
	 * Gets the clientId for the provider
	 * 
	 * @return the client id
	 */
	String getClientId();

	/**
	 * Takes an authorization code and exchanges it for an access token. Instead of returning a
	 * token the token is used to get the user identifier
	 * 
	 * @param code the authorization code
	 * @return the user context
	 */
	UserContext authorizationCodeToUserContext(String code);

	/**
	 * Constructs the redirect URI for the provider.
	 * 
	 * @param state the state
	 * @return the redirect uri
	 */
	String getAuthorizationRedirectUri(String state);
}
