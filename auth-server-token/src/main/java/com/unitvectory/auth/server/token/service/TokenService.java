package com.unitvectory.auth.server.token.service;

import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwk.InvalidPublicKeyException;
import com.auth0.jwk.Jwk;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.unitvectory.auth.common.service.entropy.EntropyService;
import com.unitvectory.auth.common.service.time.TimeService;
import com.unitvectory.auth.datamodel.model.Authorization;
import com.unitvectory.auth.datamodel.model.Client;
import com.unitvectory.auth.datamodel.model.ClientJwtBearer;
import com.unitvectory.auth.datamodel.repository.AuthorizationRepository;
import com.unitvectory.auth.datamodel.repository.ClientRepository;
import com.unitvectory.auth.server.token.dto.TokenRequest;
import com.unitvectory.auth.server.token.dto.TokenResponse;
import com.unitvectory.auth.server.token.model.JwtBuilder;
import com.unitvectory.auth.server.token.service.jwk.JwksService;
import com.unitvectory.auth.sign.service.SignService;
import com.unitvectory.auth.util.exception.BadRequestException;
import com.unitvectory.auth.util.exception.ForbiddenException;
import com.unitvectory.auth.util.exception.InternalServerErrorException;
import com.unitvectory.auth.util.exception.UnauthorizedException;

@Service
public class TokenService {

	@Value("${serviceauthcentral.server.token.issuer}")
	private String issuer;

	@Autowired
	private AuthorizationRepository authorizationRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private SignService signService;

	@Autowired
	private JwksService jwksService;

	@Autowired
	private TimeService timeService;

	@Autowired
	private EntropyService entropyService;

	private TokenResponse buildToken(Client subjectRecord, Client audienceRecord, Authorization authorizationRecord) {

		String clientId = subjectRecord.getClientId();
		String audience = audienceRecord.getClientId();

		long now = this.timeService.getCurrentTimeSeconds();

		// Get the active key
		String kid = this.signService.getActiveKid(now);
		if (kid == null) {
			throw new InternalServerErrorException("No active signing keys, cannot generate access token.");
		}

		// How long the JWT is valid
		long validSeconds = 3600;

		// Generate the unsigned JWT
		JwtBuilder builder = JwtBuilder.builder();
		builder.withIssuer(this.issuer);
		builder.withTiming(timeService.getCurrentTimeSeconds(), validSeconds);
		builder.withJwtId(entropyService.generateUuid());
		builder.withKeyId(kid);
		builder.withSubject(clientId);
		builder.withAudience(audience);
		String unsignedJwt = builder.buildUnsignedToken();

		// Sign the JWT
		String jwt = this.signService.sign(kid, unsignedJwt);

		// Build the response
		return TokenResponse.builder().withAccess_token(jwt).withExpires_in(validSeconds).withToken_type("Bearer")
				.build();
	}

	public boolean isValidToken(DecodedJWT jwt, Jwk jwk, ClientJwtBearer jwtBearer) {
		try {
			String alg = jwt.getAlgorithm(); // Get the algorithm from the JWT
			Algorithm algorithm;

			switch (alg) {
			case "RS256":
				RSAPublicKey rsaPublicKey = (RSAPublicKey) jwk.getPublicKey();
				algorithm = Algorithm.RSA256(rsaPublicKey, null);
				break;
			case "ES256":
				ECPublicKey ecPublicKey = (ECPublicKey) jwk.getPublicKey();
				algorithm = Algorithm.ECDSA256(ecPublicKey, null);
				break;
			// Add cases for other algorithms
			default:
				throw new InternalServerErrorException("Unsupported algorithm: " + alg);
			}

			// Build a JWTVerifier with the algorithm
			JWTVerifier verifier = JWT.require(algorithm).withIssuer(jwtBearer.getIss())
					.withAudience(jwtBearer.getAud()).withSubject(jwtBearer.getSub()).build();

			// Verify the DecodedJWT
			verifier.verify(jwt);

			return true; // Token is valid
		} catch (JWTVerificationException e) {
			// Invalid token
			throw new ForbiddenException(e.getMessage());
		} catch (InvalidPublicKeyException e) {
			throw new InternalServerErrorException("invalid public key", e);
		}
	}

	public TokenResponse jwtAssertion(TokenRequest request) throws Exception {

		String clientId = request.getClient_id();
		String audience = request.getAudience();

		String assertion = request.getAssertion();
		if (assertion == null || assertion.isEmpty()) {
			throw new BadRequestException("The request is missing the required parameter 'assertion'.");
		}

		DecodedJWT assertionJwt = JWT.decode(assertion);

		if (request.getClient_secret() != null) {
			throw new BadRequestException("The request has unexpected parameter 'client_secret'.");
		}

		// Get the subject record
		Client subjectRecord = clientRepository.getClient(clientId);
		if (subjectRecord == null) {
			throw new UnauthorizedException("The request has invalid 'client_id'.");
		}

		if (subjectRecord.getJwtBearer() == null) {
			throw new ForbiddenException("The client not configured for jwt-bearer.");
		}

		// Without validating the signature, determine if one matches
		ClientJwtBearer jwtMatchedBearer = null;
		for (ClientJwtBearer jwtBearer : subjectRecord.getJwtBearer()) {
			if (matches(jwtBearer, assertionJwt)) {
				jwtMatchedBearer = jwtBearer;
				break;
			}
		}

		// None matched, no need to check signature
		if (jwtMatchedBearer == null) {
			throw new ForbiddenException("The client no matching jwt-bearer.");
		}

		// Look up the JWK, this will grabbed the cached version if possible
		Jwk jwk = this.jwksService.getJwk(jwtMatchedBearer.getJwksUrl(), assertionJwt.getKeyId());

		// Validate the JWT
		this.isValidToken(assertionJwt, jwk, jwtMatchedBearer);

		// Get the audience record
		Client audienceRecord = clientRepository.getClient(audience);
		if (audienceRecord == null) {
			throw new ForbiddenException("The specified 'audience' is invalid.");
		}

		// Validated the authorization
		Authorization authorizationRecord = this.authorizationRepository.getAuthorization(clientId, audience);
		if (authorizationRecord == null) {
			throw new ForbiddenException("The specified 'audience' is invalid.");
		}

		// Build the JWT and return it
		return buildToken(subjectRecord, audienceRecord, authorizationRecord);
	}

	public TokenResponse clientCredentials(TokenRequest request) throws Exception {

		String clientId = request.getClient_id();
		String audience = request.getAudience();

		String clientSecret = request.getClient_secret();
		if (clientSecret == null || clientSecret.isEmpty()) {
			throw new BadRequestException("The request is missing the required parameter 'client_secret'.");
		}

		if (request.getAssertion() != null) {
			throw new BadRequestException("The request has unexpected parameter 'assertion'.");
		}

		// Get the subject record
		Client subjectRecord = clientRepository.getClient(clientId);
		if (subjectRecord == null) {
			throw new UnauthorizedException("The request has invalid 'client_id'.");
		}

		// Verify the client_secret
		if (!subjectRecord.verifySecret(clientSecret)) {
			throw new UnauthorizedException("The request has invalid 'client_id' or 'client_secret.");
		}

		// Get the audience record
		Client audienceRecord = clientRepository.getClient(audience);
		if (audienceRecord == null) {
			throw new ForbiddenException("The specified 'audience' is invalid.");
		}

		// Validated the authorization
		Authorization authorizationRecord = this.authorizationRepository.getAuthorization(clientId, audience);
		if (authorizationRecord == null) {
			throw new ForbiddenException("The specified 'audience' is invalid.");
		}

		// Build the JWT and return it
		return buildToken(subjectRecord, audienceRecord, authorizationRecord);
	}

	private boolean matches(ClientJwtBearer bearer, DecodedJWT jwt) {
		if (bearer == null || jwt == null) {
			return false;
		}

		// Verify that 'iss', 'sub', and 'aud' are not null in this class
		if (bearer.getIss() == null || bearer.getSub() == null || bearer.getAud() == null) {
			return false;
		}

		// Check if 'iss', 'sub', and 'aud' match between this class and the decoded JWT
		boolean issMatch = bearer.getIss().equals(jwt.getIssuer());
		boolean subMatch = bearer.getSub().equals(jwt.getSubject());
		boolean audMatch = jwt.getAudience().contains(bearer.getAud());

		return issMatch && subMatch && audMatch;
	}
}
