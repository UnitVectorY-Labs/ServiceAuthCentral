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
package com.unitvectory.serviceauthcentral.sign.service;

import java.util.List;

import com.unitvectory.serviceauthcentral.sign.model.SignJwk;

/**
 * Service interface for signing operations and key management for JWTs and JWKS.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public interface SignService {

	/**
	 * Retrieves the Key ID (KID) of the active key to be used for signing at the given time.
	 *
	 * @param now The current time in seconds.
	 * @return the active kid.
	 */
	String getActiveKid(long now);

	/**
	 * Signs a given token using the key identified by the provided Key ID (KID).
	 *
	 * @param kid The kid of the key to be used for signing.
	 * @param unsignedToken The token to be signed.
	 * @return the signed token.
	 */
	String sign(String kid, String unsignedToken);

	/**
	 * Retrieves all available JSON Web Keys (JWKs).
	 *
	 * @return a list of all JSON Web Keys.
	 */
	List<SignJwk> getAll();
}
