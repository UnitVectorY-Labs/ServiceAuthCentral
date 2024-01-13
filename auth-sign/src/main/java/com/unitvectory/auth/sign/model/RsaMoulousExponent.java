package com.unitvectory.auth.sign.model;

import lombok.Builder;
import lombok.Value;

/**
 * Represents the modulus and exponent of an RSA public key.
 */
@Value
@Builder
public class RsaMoulousExponent {

	/**
	 * The modulus of the RSA key. It's a base64 URL encoded string. The modulus is a part of the
	 * public key used in cryptographic operations.
	 *
	 * @return the modulus of the RSA key.
	 */
	private final String modulus;

	/**
	 * The exponent of the RSA key. It's a base64 URL encoded string. The exponent is a part of the
	 * public key used in cryptographic operations.
	 *
	 * @return the exponent of the RSA key.
	 */
	private final String exponent;
}
