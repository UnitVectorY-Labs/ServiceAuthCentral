package com.unitvectory.auth.verify.auth0.service;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import com.unitvectory.auth.util.exception.BadRequestException;
import com.unitvectory.auth.util.exception.ForbiddenException;
import com.unitvectory.auth.util.exception.InternalServerErrorException;
import com.unitvectory.auth.verify.model.VerifyJwk;
import com.unitvectory.auth.verify.model.VerifyJwt;
import com.unitvectory.auth.verify.model.VerifyParameters;
import com.unitvectory.auth.verify.service.JwtVerifier;

import lombok.NonNull;

public class Auth0JwtVerifier implements JwtVerifier {

	@Override
	public VerifyJwt extractClaims(@NonNull String jwt) {

		DecodedJWT decodedJwt = JWT.decode(jwt);

		VerifyJwt.VerifyJwtBuilder verifyJwt = VerifyJwt.builder();

		verifyJwt.kid(decodedJwt.getKeyId());

		verifyJwt.iss(decodedJwt.getIssuer());

		verifyJwt.sub(decodedJwt.getSubject());

		List<String> aud = decodedJwt.getAudience();
		if (aud.size() == 0) {
			verifyJwt.aud(null);
		} else if (aud.size() == 1) {
			verifyJwt.aud(decodedJwt.getAudience().get(0));
		} else {
			throw new BadRequestException("multiple audiences defined in JWT not supported");
		}
		return verifyJwt.build();
	}

	@Override
	public boolean verifySignature(@NonNull String token, @NonNull VerifyJwk jwk,
			@NonNull VerifyParameters verifyParameters) {

		// Current limitations for verification
		if (!"sig".equals(jwk.getUse())) {
			throw new ForbiddenException("jwt verification only works for jwk with 'use' of 'sig'");
		} else if (!"RSA".equals(jwk.getKty())) {
			throw new ForbiddenException("jwt verification only works for jwk with 'kty' of 'RSA'");
		} else if (!"RS256".equals(jwk.getAlg())) {
			throw new ForbiddenException(
					"jwt verification only works for jwk with 'alg' of 'RS256'");
		}

		Algorithm algorithm = Algorithm.RSA256(convertRsaKey(jwk), null);

		Verification verification = JWT.require(algorithm);

		if (verifyParameters.getIss() != null) {
			verification = verification.withIssuer(verifyParameters.getIss());
		}

		if (verifyParameters.getSub() != null) {
			verification = verification.withSubject(verifyParameters.getSub());
		}

		if (verifyParameters.getAud() != null) {
			verification = verification.withAudience(verifyParameters.getAud());
		}

		JWTVerifier verifier = verification.build();

		try {
			verifier.verify(token);
			return true;
		} catch (JWTVerificationException e) {
			// Invalid token
			throw new ForbiddenException(e.getMessage());
		}
	}

	private static RSAPublicKey convertRsaKey(VerifyJwk jwk) {
		try {
			// Decode the Base64 URL-encoded parts
			byte[] modulusBytes = Base64.getUrlDecoder().decode(jwk.getN());
			byte[] exponentBytes = Base64.getUrlDecoder().decode(jwk.getE());

			// Convert byte[] to BigInteger
			BigInteger modulus = new BigInteger(1, modulusBytes);
			BigInteger exponent = new BigInteger(1, exponentBytes);

			// Create RSAPublicKeySpec
			RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, exponent);

			// Get KeyFactory and generate RSAPublicKey
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return (RSAPublicKey) keyFactory.generatePublic(rsaPublicKeySpec);
		} catch (Exception e) {
			throw new InternalServerErrorException(e);
		}
	}
}
