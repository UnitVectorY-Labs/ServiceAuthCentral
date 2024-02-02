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
 * Interface representing a cached JSON Web Key (JWK).
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public interface CachedJwk {

	/**
	 * Determines if this JWK record is valid. Invalid records are tombstones that indicate the kid
	 * for the JWK does not exist.
	 *
	 * @return true if the record is valid, false otherwise.
	 */
	boolean isValid();

	/**
	 * Checks if the JWK record is expired.
	 *
	 * @param now the current time in seconds since the epoch.
	 * @return true if the record is expired, false otherwise.
	 */
	boolean isExpired(long now);

	/**
	 * Returns the Key ID (kid) of this JWK.
	 *
	 * @return the Key ID.
	 */
	String getKid();

	/**
	 * Returns the Key Type (kty) of this JWK.
	 *
	 * @return the Key Type.
	 */
	String getKty();

	/**
	 * Returns the algorithm (alg) parameter that identifies the algorithm intended for use with the
	 * key.
	 *
	 * @return the algorithm used.
	 */
	String getAlg();

	/**
	 * Returns the Public Key Use (use) parameter that identifies the intended use of the public
	 * key.
	 *
	 * @return the intended use of the public key.
	 */
	String getUse();

	/**
	 * Returns the modulus (n) for RSA keys.
	 *
	 * @return the modulus for RSA keys.
	 */
	String getN();

	/**
	 * Returns the exponent (e) for RSA keys.
	 *
	 * @return the exponent for RSA keys.
	 */
	String getE();
}
