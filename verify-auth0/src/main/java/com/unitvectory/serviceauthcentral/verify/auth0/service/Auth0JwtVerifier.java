/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.unitvectory.serviceauthcentral.verify.auth0.service;

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
import com.unitvectory.serviceauthcentral.util.exception.BadRequestException;
import com.unitvectory.serviceauthcentral.util.exception.ForbiddenException;
import com.unitvectory.serviceauthcentral.util.exception.InternalServerErrorException;
import com.unitvectory.serviceauthcentral.verify.model.VerifyJwk;
import com.unitvectory.serviceauthcentral.verify.model.VerifyJwt;
import com.unitvectory.serviceauthcentral.verify.model.VerifyParameters;
import com.unitvectory.serviceauthcentral.verify.service.JwtVerifier;

import lombok.NonNull;

/**
 * The JWT Verifier that utilizies Auth0's client library.
 * 
 * This does not depend on the Auth0 service, just their library implementation.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
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
