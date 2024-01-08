package com.unitvectory.auth.sign.model;

/**
 * Interface representing a JSON Web Key (JWK).
 */
public interface SignJwk {

	/**
	 * Returns the Key Type (kty) of this JWK.
	 * 
	 * @return the Key Type.
	 */
	String getKty();

	/**
	 * Returns the Key ID (kid) of this JWK.
	 * 
	 * @return the Key ID.
	 */
	String getKid();

	/**
	 * Returns the Public Key Use (use) parameter that identifies the intended use
	 * of the public key.
	 * 
	 * @return the intended use of the public key.
	 */
	String getUse();

	/**
	 * Returns the algorithm (alg) parameter that identifies the algorithm intended
	 * for use with the key.
	 * 
	 * @return the algorithm used.
	 */
	String getAlg();

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
