package com.unitvectory.auth.sign.service;

import java.util.List;

import com.unitvectory.auth.sign.model.JsonWebKey;

/**
 * Service interface for signing operations and key management for JWTs and
 * JWKS.
 */
public interface SignService {

	/**
	 * Retrieves the Key ID (KID) of the active key to be used for signing at the
	 * given time.
	 *
	 * @param now The current time in milliseconds.
	 * @return the active kid.
	 */
	String getActiveKid(long now);

	/**
	 * Signs a given token using the key identified by the provided Key ID (KID).
	 *
	 * @param kid           The kid of the key to be used for signing.
	 * @param unsignedToken The token to be signed.
	 * @return the signed token.
	 */
	String sign(String kid, String unsignedToken);

	/**
	 * Retrieves all available JSON Web Keys (JWKs).
	 *
	 * @return a list of all JSON Web Keys.
	 */
	List<JsonWebKey> getAll();
}
