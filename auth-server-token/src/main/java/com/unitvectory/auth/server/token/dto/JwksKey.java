package com.unitvectory.auth.server.token.dto;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.unitvectory.auth.server.token.util.KidConverter;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(setterPrefix = "with")
@JsonInclude(Include.NON_NULL)
public class JwksKey {

	private String kty;

	private String kid;

	private String use;

	private String alg;

	private String n;

	private String e;

	/*
	 * Attributes not returned, but needed to determine which key to use when
	 * signing the JWT
	 */

	@JsonIgnore
	private String keyName;

	@JsonIgnore
	private boolean active;

	@JsonIgnore
	private long created;

	public static JwksKey convertRsaPublicKey(String keyName, String pemKey, String alg, boolean active, long created) {

		// Generate the Key ID by hashing the name of the key (including version)
		String kid = KidConverter.hash(keyName);

		// Extract the encoded key part and decode it
		String publicKeyPEM = pemKey.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "")
				.replaceAll("\\s", "");

		byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);

		// Convert to RSA Public Key
		java.security.PublicKey publicKey;
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
			publicKey = keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// Extract modulus and exponent from the public key
		java.security.interfaces.RSAPublicKey rsaPublicKey = (java.security.interfaces.RSAPublicKey) publicKey;

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

		// Construct the JWKS JSON object
		return JwksKey.builder().withKty("RSA").withN(modulus).withE(exponent).withAlg(alg).withKid(kid).withUse("sig")
				.withKeyName(keyName).withActive(active).withCreated(created).build();
	}
}
