package com.unitvectory.auth.datamodel.model;

public interface CachedJwk {

	// Not a JWK attribute, a marker to indicate this record is valid
	boolean isValid();

	// Not a JWK attribute; indicates if the record exists but is expired
	boolean isExpired(long now);

	// Key ID
	String getKid();

	// Key Type
	String getKty();

	// Algorithm
	String getAlg();

	// Public Key Use
	String getUse();

	// Modulus for RSA keys
	String getN();

	// Exponent for RSA keys
	String getE();
}
