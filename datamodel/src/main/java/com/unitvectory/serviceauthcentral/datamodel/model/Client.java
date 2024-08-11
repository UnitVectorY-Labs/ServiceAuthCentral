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

import com.unitvectory.serviceauthcentral.util.HashingUtil;

import lombok.NonNull;

/**
 * Interface representing a client with its details and secrets.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public interface Client {

	/**
	 * Returns the timestamp the client was created
	 * 
	 * @return the timestamp
	 */
	String getClientCreated();

	/**
	 * Returns the unique identifier for the client.
	 *
	 * @return the client ID.
	 */
	String getClientId();

	/**
	 * Returns the client type.
	 * 
	 * @return the client type
	 */
	ClientType getClientType();

	/**
	 * Returns a description of the client.
	 *
	 * @return the client description.
	 */
	String getDescription();

	/**
	 * Returns the salt used for hashing secrets.
	 *
	 * @return the salt.
	 */
	String getSalt();

	/**
	 * Returns the first client secret as hashed value; may be null if not set.
	 *
	 * @return the first client secret.
	 */
	String getClientSecret1();

	/**
	 * Returns the timestamp the clientSecret1 was updated.
	 * 
	 * @return the timestamp
	 */
	String getClientSecret1Updated();

	/**
	 * Returns the second client secret as hashed value; may be null if not set.
	 *
	 * @return the second client secret.
	 */
	String getClientSecret2();


	/**
	 * Returns the timestamp the clientSecret2 was updated.
	 * 
	 * @return the timestamp
	 */
	String getClientSecret2Updated();

	/**
	 * Returns the list of available scopes associated with the client.
	 *
	 * @return the list of scopes.
	 */
	List<ClientScope> getAvailableScopes();

	/**
	 * Returns a list of JWT Bearer details associated with the client.
	 * 
	 * These can be used in place of a secret for authentication.
	 *
	 * @return the list of JWT Bearer details.
	 */
	List<ClientJwtBearer> getJwtBearer();

	/**
	 * Hashes a secret using SHA-256 and the client's salt.
	 * 
	 * Used as a helper method when setting a client secret.
	 *
	 * @param secret the secret to hash.
	 * @return the hashed secret.
	 * @throws IllegalStateException if the salt is not set.
	 */
	public default String hashSecret(@NonNull String secret) {
		if (this.getSalt() == null) {
			throw new IllegalStateException("Salt not set");
		}

		String hashedSecret = HashingUtil.sha256(secret);
		return HashingUtil.sha256(hashedSecret + this.getSalt());
	}

	/**
	 * Verifies a secret against the stored client secrets.
	 *
	 * @param secret the secret to verify.
	 * @return true if the secret matches either of the stored secrets, false otherwise.
	 */
	public default boolean verifySecret(@NonNull String secret) {

		if (this.getSalt() == null) {
			return false;
		}

		// Hash the secret so it can be compared
		String hashedSecret = this.hashSecret(secret);

		if (this.getClientSecret1() != null && this.getClientSecret1().equals(hashedSecret)) {
			return true;
		}

		if (this.getClientSecret2() != null && this.getClientSecret2().equals(hashedSecret)) {
			return true;
		}

		return false;
	}

	/**
	 * Returns true if the client has the specified scope.
	 *
	 * @param scope the scope to check.
	 * @return true if the client has the scope, false otherwise.
	 */
	public default boolean hasScope(@NonNull String scope) {
		if (this.getAvailableScopes() == null) {
			return false;
		}

		for (ClientScope clientScope : this.getAvailableScopes()) {
			if (clientScope.getScope().equals(scope)) {
				return true;
			}
		}

		return false;
	}
}
