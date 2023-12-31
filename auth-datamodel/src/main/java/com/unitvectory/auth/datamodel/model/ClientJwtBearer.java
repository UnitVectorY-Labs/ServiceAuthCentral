package com.unitvectory.auth.datamodel.model;

/**
 * Interface representing a client with JWT Bearer details.
 * 
 * The details here are used to authenticate JWTs against a client in place of a
 * secret.
 */
public interface ClientJwtBearer {

	/**
	 * Returns the URL of the JSON Web Key Set (JWKS).
	 * 
	 * This provides where the JWKS will be retrieved independent of what is
	 * specified in the JWT.
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
	 * Returns the subject (sub) claim identifying the principal that is the subject
	 * of the JWT.
	 *
	 * The JWT must exactly match the `sub` claim specified.
	 * 
	 * @return the subject.
	 */
	String getSub();

	/**
	 * Returns the audience (aud) claim identifying the recipients that the JWT is
	 * intended for.
	 *
	 * The JWT must exactly contain the `aud` claim specified.
	 * 
	 * @return the audience.
	 */
	String getAud();
}
