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
package com.unitvectory.serviceauthcentral.datamodel.model;

/**
 * Interface representing the OAuth authorization code record.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
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

	/**
	 * Return the time to live
	 * 
	 * @return the time to live
	 */
	long getTimeToLive();

}
