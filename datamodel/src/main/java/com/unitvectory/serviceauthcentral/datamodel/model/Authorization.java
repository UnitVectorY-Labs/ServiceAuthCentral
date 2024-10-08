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

import java.util.List;
import lombok.NonNull;

/**
 * Authorization document interface
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public interface Authorization {

	/**
	 * the document id is intended to be a combination of the subject and audience
	 * 
	 * @return the document id uniquely identifying this document
	 */
	String getDocumentId();

	/**
	 * Returns the timestamp the authorization was created
	 * 
	 * @return the timestamp
	 */
	String getAuthorizationCreated();

	/**
	 * The client that is authorized as the subject for a token exchange
	 * 
	 * @return the clientId for the subject
	 */
	String getSubject();

	/**
	 * The audience that is authorized as the audience for a token exchange
	 * 
	 * @return the clientId for the audience
	 */
	String getAudience();

	/**
	 * The list of authorized scopes.
	 * 
	 * @return the list of scopes
	 */
	List<String> getAuthorizedScopes();

	/**
	 * Returns true if the authorization has the specified scope.
	 *
	 * @param scope the scope to check.
	 * @return true if the client has the scope, false otherwise.
	 */
	public default boolean hasScope(@NonNull String scope) {
		if (this.getAuthorizedScopes() == null) {
			return false;
		}

		return this.getAuthorizedScopes().contains(scope);
	}
}
