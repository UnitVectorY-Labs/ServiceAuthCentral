package com.unitvectory.serviceauthcentral.service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.hash.Hashing;
import com.unitvectory.serviceauthcentral.dto.JwksKey;
import com.unitvectory.serviceauthcentral.model.JwtBuilder;
import com.unitvectory.serviceauthcentral.service.entropy.EntropyService;
import com.unitvectory.serviceauthcentral.service.time.TimeService;

@Service
public class CryptoService {

	@Autowired
	private TimeService timeService;

	@Autowired
	private EntropyService entropyService;

	@Value("${serviceauthcentral.jwt.issuer}")
	private String jwtIssuer;

	public String buildUnsignedJwt(String keyName, String subject, String audience, long validSeconds) {
		JwtBuilder builder = JwtBuilder.builder();
		builder.withIssuer(jwtIssuer);
		builder.withTiming(timeService.getCurrentTimeSeconds(), validSeconds);
		builder.withJwtId(entropyService.generateUuid());
		builder.withKeyId(kid(keyName));
		builder.withSubject(subject);
		builder.withAudience(audience);
		return builder.buildUnsignedToken();
	}

	public JwksKey convertRsaPublicKey(String keyName, String pemKey, String alg, boolean active, long created) {

		// Generate the Key ID by hashing the name of the key (including version)
		String kid = kid(keyName);

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

	private String kid(String keyName) {
		// This conversion must match between the key used to sign a JWT and the JWKS so
		// it can be validated properly. This means the keyName must include the full
		// key including the version
		return Hashing.sha256().hashString(keyName, StandardCharsets.UTF_8).toString();
	}
}
