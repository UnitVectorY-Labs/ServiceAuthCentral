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
package com.unitvectory.auth.datamodel.repository;

import com.unitvectory.auth.datamodel.model.Client;
import com.unitvectory.auth.datamodel.model.ClientSummaryConnection;
import com.unitvectory.auth.datamodel.model.ClientType;

/**
 * Interface for repository handling the storage, retrieval, and management of client details.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public interface ClientRepository {

	/**
	 * Retrieves a page of clients using cursor-based pagination.
	 * 
	 * @param first The number of records to retrieve after the 'after' cursor. Used for forward
	 *        pagination.
	 * @param after The cursor indicating the starting point for forward pagination; null indicates
	 *        the start.
	 * @param last The number of records to retrieve before the 'before' cursor. Used for reverse
	 *        pagination.
	 * @param before The cursor indicating the starting point for reverse pagination; null indicates
	 *        the end.
	 * @return ClientSummaryConnection representing a paginated list of clients, including PageInfo
	 *         with cursors for subsequent queries.
	 */
	ClientSummaryConnection getClients(Integer first, String after, Integer last, String before);

	/**
	 * Retrieves a client by its unique identifier.
	 *
	 * @param clientId the unique identifier for the client.
	 * @return the Client object associated with the specified clientId, or null if not found.
	 */
	Client getClient(String clientId);

	/**
	 * Deletes a client record
	 * 
	 * @param clientId the identifier to delete
	 */
	void deleteClient(String clientId);

	/**
	 * Stores or updates a client with the given client ID, description, and salt.
	 *
	 * @param clientId the unique identifier for the client.
	 * @param description a brief description of the client.
	 * @param salt the salt to be used for hashing client secrets.
	 * @param clientType the client type
	 */
	void putClient(String clientId, String description, String salt, ClientType clientType);

	/**
	 * Adds an authorized JWT to a client
	 * 
	 * @param clientId the clientId to modify
	 * @param id the unique id for the record
	 * @param jwksUrl the JWKS URL to validate the JWT
	 * @param iss the issuer for the token to validate
	 * @param sub the subject for the token to validate
	 * @param aud the audience for the token to validate
	 */
	void addAuthorizedJwt(String clientId, String id, String jwksUrl, String iss, String sub,
			String aud);

	/**
	 * Removes an authorized JWT from a client
	 * 
	 * @param clientId the clientId to modify
	 * @param id the unique identifier
	 */
	void removeAuthorizedJwt(String clientId, String id);

	/**
	 * Saves or updates the first client secret for the specified client.
	 * 
	 * This value is the hashed value value.
	 *
	 * @param clientId the unique identifier for the client.
	 * @param hashedSecret the hashed client secret to be saved.
	 */
	void saveClientSecret1(String clientId, String hashedSecret);

	/**
	 * Saves or updates the second client secret for the specified client.
	 * 
	 * This value is the hashed value value.
	 *
	 * @param clientId the unique identifier for the client.
	 * @param hashedSecret the hashed client secret to be saved.
	 */
	void saveClientSecret2(String clientId, String hashedSecret);

	/**
	 * Clears the first client secret for the specified client.
	 *
	 * @param clientId the unique identifier for the client whose first secret is to be cleared.
	 */
	void clearClientSecret1(String clientId);

	/**
	 * Clears the second client secret for the specified client.
	 *
	 * @param clientId the unique identifier for the client whose second secret is to be cleared.
	 */
	void clearClientSecret2(String clientId);
}
