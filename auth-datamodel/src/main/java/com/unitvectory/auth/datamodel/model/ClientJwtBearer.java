package com.unitvectory.auth.datamodel.model;

import java.util.Objects;

/**
 * Interface representing a client with JWT Bearer details.
 * 
 * The details here are used to authenticate JWTs against a client in place of a secret.
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
