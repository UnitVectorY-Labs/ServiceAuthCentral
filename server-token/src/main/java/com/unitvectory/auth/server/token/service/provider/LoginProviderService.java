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
package com.unitvectory.auth.server.token.service.provider;

import com.unitvectory.auth.server.token.model.UserContext;

/**
 * Interface to provide separate implementations for OAuth login provider.
 * 
 * This allows for multiple OAuth providers to be implemented in the context of the double OAuth
 * flow facilitated with PKCE to the front end web application
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
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
