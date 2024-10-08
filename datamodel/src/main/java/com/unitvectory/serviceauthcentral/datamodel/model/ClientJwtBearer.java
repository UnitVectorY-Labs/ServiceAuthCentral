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

import java.util.Objects;

/**
 * Interface representing a client with JWT Bearer details.
 * 
 * The details here are used to authenticate JWTs against a client in place of a secret.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public interface ClientJwtBearer {

	/**
	 * Returns the unique id for the record
	 * 
	 * @return the id
	 */
	String getId();

	/**
	 * Returns the URL of the JSON Web Key Set (JWKS).
	 * 
	 * This provides where the JWKS will be retrieved independent of what is specified in the JWT.
	 *
	 * @return the JWKS URL.
	 */
	String getJwksUrl();

	/**
	 * Returns the issuer (iss) claim identifying the principal that issued the JWT.
	 *
	 * The JWT must exactly match the `iss` claim specified.
	 * 
	 * @return the issuer.
	 */
	String getIss();

	/**
	 * Returns the subject (sub) claim identifying the principal that is the subject of the JWT.
	 *
	 * The JWT must exactly match the `sub` claim specified.
	 * 
	 * @return the subject.
	 */
	String getSub();

	/**
	 * Returns the audience (aud) claim identifying the recipients that the JWT is intended for.
	 *
	 * The JWT must exactly contain the `aud` claim specified.
	 * 
	 * @return the audience.
	 */
	String getAud();

	/**
	 * Compares a ClientJwtBearer to see if it matches. This means all fields match with the
	 * exception of id.
	 * 
	 * @param obj ClientJwtBearer
	 * @return true if matches; otherwise false
	 */
	default boolean matches(ClientJwtBearer obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		return Objects.equals(this.getJwksUrl(), obj.getJwksUrl())
				&& Objects.equals(this.getIss(), obj.getIss())
				&& Objects.equals(this.getSub(), obj.getSub())
				&& Objects.equals(this.getAud(), obj.getAud());
	}

	/**
	 * Compare a ClientJwtBearer to see if it matches.
	 * 
	 * @param jwksUrl the jwksUrl
	 * @param iss the issuer
	 * @param sub the subject
	 * @param aud the audience
	 * @return true if matches; otherwise false
	 */
	default boolean matches(String jwksUrl, String iss, String sub, String aud) {
		return Objects.equals(this.getJwksUrl(), jwksUrl) && Objects.equals(this.getIss(), iss)
				&& Objects.equals(this.getSub(), sub) && Objects.equals(this.getAud(), aud);
	}
}
