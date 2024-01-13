package com.unitvectory.auth.datamodel.repository;

import java.util.Iterator;

import com.unitvectory.auth.datamodel.model.Authorization;

/**
 * Interface for repository handling the storage and retrieval of Authorization documents.
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
	 */
	void authorize(String subject, String audience);

	/**
	 * Removes an authorization, preventing the specified subject from acting for the specified
	 * audience.
	 *
	 * @param subject the clientId representing the subject of the authorization.
	 * @param audience the clientId representing the audience of the authorization.
	 */
	void deauthorize(String subject, String audience);
}
