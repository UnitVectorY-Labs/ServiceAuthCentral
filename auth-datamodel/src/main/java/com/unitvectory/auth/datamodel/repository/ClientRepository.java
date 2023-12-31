package com.unitvectory.auth.datamodel.repository;

import com.unitvectory.auth.datamodel.model.Client;

/**
 * Interface for repository handling the storage, retrieval, and management of
 * client details.
 */
public interface ClientRepository {

	/**
	 * Retrieves a client by its unique identifier.
	 *
	 * @param clientId the unique identifier for the client.
	 * @return the Client object associated with the specified clientId, or null if
	 *         not found.
	 */
	Client getClient(String clientId);

	/**
	 * Stores or updates a client with the given client ID, description, and salt.
	 *
	 * @param clientId    the unique identifier for the client.
	 * @param description a brief description of the client.
	 * @param salt        the salt to be used for hashing client secrets.
	 */
	void putClient(String clientId, String description, String salt);

	/**
	 * Saves or updates the first client secret for the specified client.
	 * 
	 * This value is the hashed value value.
	 *
	 * @param clientId     the unique identifier for the client.
	 * @param hashedSecret the hashed client secret to be saved.
	 */
	void saveClientSecret1(String clientId, String hashedSecret);

	/**
	 * Saves or updates the second client secret for the specified client.
	 * 
	 * This value is the hashed value value.
	 *
	 * @param clientId     the unique identifier for the client.
	 * @param hashedSecret the hashed client secret to be saved.
	 */
	void saveClientSecret2(String clientId, String hashedSecret);

	/**
	 * Clears the first client secret for the specified client.
	 *
	 * @param clientId the unique identifier for the client whose first secret is to
	 *                 be cleared.
	 */
	void clearClientSecret1(String clientId);

	/**
	 * Clears the second client secret for the specified client.
	 *
	 * @param clientId the unique identifier for the client whose second secret is
	 *                 to be cleared.
	 */
	void clearClientSecret2(String clientId);
}
