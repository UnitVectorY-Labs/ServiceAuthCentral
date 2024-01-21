package com.unitvectory.auth.server.token.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.unitvectory.auth.common.service.entropy.EntropyService;
import com.unitvectory.auth.common.service.time.TimeService;
import com.unitvectory.auth.datamodel.model.Authorization;
import com.unitvectory.auth.datamodel.model.Client;
import com.unitvectory.auth.datamodel.model.ClientJwtBearer;
import com.unitvectory.auth.datamodel.model.LoginCode;
import com.unitvectory.auth.datamodel.repository.AuthorizationRepository;
import com.unitvectory.auth.datamodel.repository.ClientRepository;
import com.unitvectory.auth.datamodel.repository.LoginCodeRepository;
import com.unitvectory.auth.server.token.dto.TokenRequest;
import com.unitvectory.auth.server.token.dto.TokenResponse;
import com.unitvectory.auth.server.token.model.JwtBuilder;
import com.unitvectory.auth.sign.service.SignService;
import com.unitvectory.auth.util.HashingUtil;
import com.unitvectory.auth.util.exception.BadRequestException;
import com.unitvectory.auth.util.exception.ForbiddenException;
import com.unitvectory.auth.util.exception.InternalServerErrorException;
import com.unitvectory.auth.util.exception.UnauthorizedException;
import com.unitvectory.auth.verify.model.VerifyJwk;
import com.unitvectory.auth.verify.model.VerifyJwt;
import com.unitvectory.auth.verify.model.VerifyParameters;
import com.unitvectory.auth.verify.service.JwtVerifier;

@Service
public class TokenService {

	@Value("${serviceauthcentral.server.token.issuer}")
	private String issuer;

	@Autowired
	private AuthorizationRepository authorizationRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	public LoginCodeRepository loginCodeRepository;

	@Autowired
	private SignService signService;

	@Autowired
	private TimeService timeService;

	@Autowired
	private EntropyService entropyService;

	@Autowired
	private ExternalJwkService externalJwkService;

	@Autowired
	private JwtVerifier jwtVerifier;

	public TokenResponse token(TokenRequest tokenRequest) throws Exception {
		String grantType = tokenRequest.getGrant_type();
		if ("client_credentials".equals(grantType)) {
			return this.clientCredentials(tokenRequest);
		} else if ("urn:ietf:params:oauth:grant-type:jwt-bearer".equals(grantType)) {
			return this.jwtAssertion(tokenRequest);
		} else if ("authorization_code".equals(tokenRequest.getGrant_type())) {
			return this.authorizationCode(tokenRequest);
		} else {
			// Invalid value for the grant type
			throw new BadRequestException("The request 'grant_type' provided is not supported.");
		}
	}

	private TokenResponse authorizationCode(TokenRequest request) throws Exception {

		if (request.getClient_secret() != null) {
			throw new BadRequestException("The request has unexpected parameter 'client_secret'.");
		} else if (request.getAudience() != null) {
			throw new BadRequestException("The request has unexpected parameter 'audience'.");
		} else if (request.getAssertion() != null) {
			throw new BadRequestException("The request has unexpected parameter 'assertion'.");
		}

		String code = request.getCode();
		String redirectUri = request.getRedirect_uri();
		String clientId = request.getClient_id();
		String codeVerifier = request.getCode_verifier();

		if (code == null || code.isEmpty()) {
			throw new BadRequestException("The request is missing the required parameter 'code'.");
		} else if (redirectUri == null || redirectUri.isEmpty()) {
			throw new BadRequestException(
					"The request is missing the required parameter 'redirect_uri'.");
		} else if (codeVerifier == null || codeVerifier.isEmpty()) {
			throw new BadRequestException(
					"The request is missing the required parameter 'code_verifier'.");
		}

		// The code needs to be in the database and valid
		LoginCode loginCode = this.loginCodeRepository.getCode(code);
		if (loginCode == null) {
			throw new BadRequestException("The request has invalid parameter 'code'.");
		}

		// The client_id passed in must match the client_id stored for the code
		if (!clientId.equals(loginCode.getClientId())) {
			throw new BadRequestException("The request has invalid parameter 'client_id'.");
		}

		// Validate the PKCE parameter
		String codeChallenge = HashingUtil.sha256(codeVerifier);
		if (!codeChallenge.equals(loginCode.getCodeChallenge())) {
			throw new BadRequestException("The request has invalid parameter 'code_verifier'.");
		}

		// Validate the redirect_uri
		if (!redirectUri.equals(loginCode.getRedirectUri())) {
			throw new BadRequestException("The request has invalid parameter 'redirect_uri'.");
		}

		// TODO: Validate that the auth code is not expired.

		Client userClient = this.clientRepository.getClient(loginCode.getUserClientId());

		// Now delete the auth code so it can't be used again

		this.loginCodeRepository.deleteCode(code);

		// Build the JWT and return it
		return buildToken(userClient, null, null);
	}

	private TokenResponse jwtAssertion(TokenRequest request) throws Exception {

		if (request.getCode() != null) {
			throw new BadRequestException("The request has unexpected parameter 'code'.");
		} else if (request.getRedirect_uri() != null) {
			throw new BadRequestException("The request has unexpected parameter 'redirect_uri'.");
		} else if (request.getCode_verifier() != null) {
			throw new BadRequestException("The request has unexpected parameter 'code_verifier'.");
		}

		String clientId = request.getClient_id();
		String audience = request.getAudience();

		if (audience == null || audience.isBlank()) {
			throw new BadRequestException(
					"The request is missing the required parameter 'audience'.");
		}

		String assertion = request.getAssertion();
		if (assertion == null || assertion.isEmpty()) {
			throw new BadRequestException(
					"The request is missing the required parameter 'assertion'.");
		}

		VerifyJwt assertionJwt = this.jwtVerifier.extractClaims(assertion);

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
		VerifyJwk jwk = this.externalJwkService.getJwk(jwtMatchedBearer.getJwksUrl(),
				assertionJwt.getKid());

		// The parameters in the JWT that will be verified
		VerifyParameters verifyParameters =
				VerifyParameters.builder().iss(jwtMatchedBearer.getIss())
						.sub(jwtMatchedBearer.getSub()).aud(jwtMatchedBearer.getAud()).build();

		// Validate the JWT
		if (!this.jwtVerifier.verifySignature(assertion, jwk, verifyParameters)) {
			throw new ForbiddenException("Invalid assertion jwt.");
		}

		// Get the audience record
		Client audienceRecord = clientRepository.getClient(audience);
		if (audienceRecord == null) {
			throw new ForbiddenException("The specified 'audience' is invalid.");
		}

		// Validated the authorization
		Authorization authorizationRecord =
				this.authorizationRepository.getAuthorization(clientId, audience);
		if (authorizationRecord == null) {
			throw new ForbiddenException("The specified 'audience' is invalid.");
		}

		// Build the JWT and return it
		return buildToken(subjectRecord, audienceRecord, authorizationRecord);
	}

	private TokenResponse clientCredentials(TokenRequest request) throws Exception {

		if (request.getCode() != null) {
			throw new BadRequestException("The request has unexpected parameter 'code'.");
		} else if (request.getRedirect_uri() != null) {
			throw new BadRequestException("The request has unexpected parameter 'redirect_uri'.");
		} else if (request.getCode_verifier() != null) {
			throw new BadRequestException("The request has unexpected parameter 'code_verifier'.");
		}

		String clientId = request.getClient_id();
		String audience = request.getAudience();

		if (audience == null || audience.isBlank()) {
			throw new BadRequestException(
					"The request is missing the required parameter 'audience'.");
		}

		String clientSecret = request.getClient_secret();
		if (clientSecret == null || clientSecret.isEmpty()) {
			throw new BadRequestException(
					"The request is missing the required parameter 'client_secret'.");
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
			throw new UnauthorizedException(
					"The request has invalid 'client_id' or 'client_secret.");
		}

		// Get the audience record
		Client audienceRecord = clientRepository.getClient(audience);
		if (audienceRecord == null) {
			throw new ForbiddenException("The specified 'audience' is invalid.");
		}

		// Validated the authorization
		Authorization authorizationRecord =
				this.authorizationRepository.getAuthorization(clientId, audience);
		if (authorizationRecord == null) {
			throw new ForbiddenException("The specified 'audience' is invalid.");
		}

		// Build the JWT and return it
		return buildToken(subjectRecord, audienceRecord, authorizationRecord);
	}

	private boolean matches(ClientJwtBearer bearer, VerifyJwt jwt) {
		if (bearer == null || jwt == null) {
			return false;
		}

		// Verify that 'iss', 'sub', and 'aud' are not null in this class
		if (bearer.getIss() == null || bearer.getSub() == null || bearer.getAud() == null) {
			return false;
		}

		// Check if 'iss', 'sub', and 'aud' match between this class and the decoded JWT
		boolean issMatch = bearer.getIss().equals(jwt.getIss());
		boolean subMatch = bearer.getSub().equals(jwt.getSub());
		boolean audMatch = bearer.getAud().equals(jwt.getAud());

		return issMatch && subMatch && audMatch;
	}

	private TokenResponse buildToken(Client subjectRecord, Client audienceRecord,
			Authorization authorizationRecord) {

		long now = this.timeService.getCurrentTimeSeconds();

		// Get the active key
		String kid = this.signService.getActiveKid(now);
		if (kid == null) {
			throw new InternalServerErrorException(
					"No active signing keys, cannot generate access token.");
		}

		// How long the JWT is valid
		long validSeconds = 3600;

		// Generate the unsigned JWT
		JwtBuilder builder = JwtBuilder.builder();
		builder.withIssuer(this.issuer);
		builder.withTiming(timeService.getCurrentTimeSeconds(), validSeconds);
		builder.withJwtId(entropyService.generateUuid());
		builder.withKeyId(kid);

		if (subjectRecord != null) {
			String clientId = subjectRecord.getClientId();
			builder.withSubject(clientId);
		}

		if (audienceRecord != null) {
			String audience = audienceRecord.getClientId();
			builder.withAudience(audience);
		}

		if (authorizationRecord != null) {
			// TODO: custom claims from the authorization can be added to the token here.
		}

		String unsignedJwt = builder.buildUnsignedToken();

		// Sign the JWT
		String jwt = this.signService.sign(kid, unsignedJwt);

		// Build the response
		return TokenResponse.builder().withAccess_token(jwt).withExpires_in(validSeconds)
				.withToken_type("Bearer").build();
	}
}
