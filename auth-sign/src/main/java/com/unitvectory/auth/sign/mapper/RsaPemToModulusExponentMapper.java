package com.unitvectory.auth.sign.mapper;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import com.unitvectory.auth.sign.model.RsaMoulousExponent;
import com.unitvectory.auth.util.exception.InternalServerErrorException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * This class provides a method to convert an RSA public key from PEM format to
 * its modulus and exponent.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RsaPemToModulusExponentMapper {

	public static RsaPemToModulusExponentMapper INSTANCE = new RsaPemToModulusExponentMapper();

	/**
	 * Converts PEM formatted RSA public key to modulus and exponent.
	 * 
	 * @param pemKey PEM formatted RSA public key. Must not be null.
	 * @return An instance of RsaMoulousExponent containing the modulus and
	 *         exponent.
	 * @throws InternalServerErrorException if the conversion fails.
	 */
	public RsaMoulousExponent convert(@NonNull String pemKey) {
		try {
			// Extract the encoded key part and decode it
			byte[] encoded = decodePemPublicKey(pemKey);

			// Convert to RSA Public Key
			RSAPublicKey publicKey = generatePublicKeyFromEncodedData(encoded);

			// Extract and format modulus and exponent
			return extractAndFormatModulusAndExponent(publicKey);
		} catch (Exception e) {
			throw new InternalServerErrorException("Failed to convert RSA PEM key", e);
		}
	}

	/**
	 * Decodes a PEM formatted public key into a byte array.
	 * 
	 * @param pemKey The PEM formatted public key.
	 * @return The decoded byte array of the key.
	 */
	private byte[] decodePemPublicKey(String pemKey) {
		String publicKeyPEM = pemKey.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "")
				.replaceAll("\\s", "");
		return Base64.getDecoder().decode(publicKeyPEM);
	}

	/**
	 * Generates a RSA public key from the encoded data.
	 * 
	 * @param encoded The byte array of the encoded public key.
	 * @return A RSAPublicKey instance from the encoded data.
	 * @throws Exception if any error occurs during key generation.
	 */
	private RSAPublicKey generatePublicKeyFromEncodedData(byte[] encoded) throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
		return (RSAPublicKey) keyFactory.generatePublic(keySpec);
	}

	/**
	 * Extracts and formats the modulus and exponent from a RSAPublicKey.
	 * 
	 * @param publicKey The RSAPublicKey to extract the information from.
	 * @return An instance of RsaMoulousExponent containing the formatted modulus
	 *         and exponent.
	 */
	private RsaMoulousExponent extractAndFormatModulusAndExponent(RSAPublicKey publicKey) {
		String modulus = extractAndFormatModulus(publicKey);
		String exponent = Base64.getUrlEncoder().withoutPadding()
				.encodeToString(publicKey.getPublicExponent().toByteArray());

		return RsaMoulousExponent.builder().exponent(exponent).modulus(modulus).build();
	}

	/**
	 * Extracts and formats the modulus from a RSAPublicKey.
	 * 
	 * @param publicKey The RSAPublicKey to extract the modulus from.
	 * @return A base64 URL encoded string of the modulus.
	 */
	private String extractAndFormatModulus(RSAPublicKey publicKey) {
		byte[] modulusNumberBytes = publicKey.getModulus().toByteArray();
		byte[] modulusUnsignedNumberBytes = modulusNumberBytes[0] == 0
				? java.util.Arrays.copyOfRange(modulusNumberBytes, 1, modulusNumberBytes.length)
				: modulusNumberBytes;

		return Base64.getUrlEncoder().withoutPadding().encodeToString(modulusUnsignedNumberBytes);
	}
}
