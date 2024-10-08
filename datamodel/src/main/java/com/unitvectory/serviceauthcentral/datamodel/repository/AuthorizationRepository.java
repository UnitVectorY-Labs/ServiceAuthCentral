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

import java.util.Iterator;
import java.util.List;
import com.unitvectory.serviceauthcentral.datamodel.model.Authorization;

/**
 * Interface for repository handling the storage and retrieval of Authorization documents.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public interface AuthorizationRepository {

	/**
	 * Retrieves an Authorization document by its unique document ID.
	 *
	 * @param id the documentId for the Authorization document.
	 * @return the Authorization document with the specified ID, or null if not found.
	 */
	Authorization getAuthorization(String id);

	/**
	 * Deletes an authorization record based on document id
	 * 
	 * @param id the document id
	 */
	void deleteAuthorization(String id);

	/**
	 * Retrieves an Authorization document based on the subject and audience.
	 *
	 * @param subject the clientId representing the subject of the authorization.
	 * @param audience the clientId representing the audience of the authorization.
	 * @return the Authorization document matching the subject and audience, or null if not found.
	 */
	Authorization getAuthorization(String subject, String audience);

	/**
	 * Retrieves an iterator over Authorization documents for a specific subject.
	 *
	 * @param subject the clientId representing the subject of the authorization.
	 * @return an Iterator of Authorization documents for the specified subject.
	 */
	Iterator<Authorization> getAuthorizationBySubject(String subject);

	/**
	 * Retrieves an iterator over Authorization documents for a specific audience.
	 *
	 * @param audience the clientId representing the audience of the authorization.
	 * @return an Iterator of Authorization documents for the specified audience.
	 */
	Iterator<Authorization> getAuthorizationByAudience(String audience);

	/**
	 * Creates an authorization allowing the specified subject to act for the specified audience.
	 *
	 * @param subject the clientId representing the subject of the authorization.
	 * @param audience the clientId representing the audience of the authorization.
	 * @param authorizedScopes the list of authorized scopes
	 */
	void authorize(String subject, String audience, List<String> authorizedScopes);

	/**
	 * Removes an authorization, preventing the specified subject from acting for the specified
	 * audience.
	 *
	 * @param subject the clientId representing the subject of the authorization.
	 * @param audience the clientId representing the audience of the authorization.
	 */
	void deauthorize(String subject, String audience);

	/**
	 * Add an authorized scope.
	 * 
	 * @param subject the clientId representing the subject of the authorization.
	 * @param audience the clientId representing the audience of the authorization.
	 * @param authorizedScope the scope
	 */
	void authorizeAddScope(String subject, String audience, String authorizedScope);

	/**
	 * Remove an authorized scope.
	 * 
	 * @param subject the clientId representing the subject of the authorization.
	 * @param audience the clientId representing the audience of the authorization.
	 * @param authorizedScope the scope
	 */
	void authorizeRemoveScope(String subject, String audience, String authorizedScope);
}
