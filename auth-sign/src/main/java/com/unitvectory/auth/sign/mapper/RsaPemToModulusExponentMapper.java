package com.unitvectory.auth.sign.mapper;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import com.unitvectory.auth.sign.model.RsaMoulousExponent;
import com.unitvectory.auth.util.exception.InternalServerErrorException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RsaPemToModulusExponentMapper {

	public static RsaPemToModulusExponentMapper INSTANCE = new RsaPemToModulusExponentMapper();

	public RsaMoulousExponent convert(@NonNull String pemKey) {

		// Extract the encoded key part and decode it
		String publicKeyPEM = pemKey.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "")
				.replaceAll("\\s", "");

		// Convert to RSA Public Key
		java.security.PublicKey publicKey;
		try {
			// Decode the key
			byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);

			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
			publicKey = keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
			throw new InternalServerErrorException("failed to convert RSA PEM key", e);
		}

		// Extract modulus and exponent from the public key
		RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;

		BigInteger modulusBigInteger = rsaPublicKey.getModulus();

		// When converting the modulus to a byte array, it's important to remove any
		// leading zero byte that might be present due to Java's handling of big
		// integers. This zero byte is not part of the actual modulus value.
		byte[] modulusNumberBytes = modulusBigInteger.toByteArray();
		byte[] modulusUnsignedNumberBytes = modulusNumberBytes[0] == 0
				? java.util.Arrays.copyOfRange(modulusNumberBytes, 1, modulusNumberBytes.length)
				: modulusNumberBytes;

		String modulus = Base64.getUrlEncoder().withoutPadding().encodeToString(modulusUnsignedNumberBytes);

		String exponent = Base64.getUrlEncoder().withoutPadding()
				.encodeToString(rsaPublicKey.getPublicExponent().toByteArray());

		return RsaMoulousExponent.builder().exponent(exponent).modulus(modulus).build();
	}
}
