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
package com.unitvectory.auth.datamodel.model;

/**
 * Interface representing the login state during the authorization code loigin flow that supports
 * PKCE.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
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
