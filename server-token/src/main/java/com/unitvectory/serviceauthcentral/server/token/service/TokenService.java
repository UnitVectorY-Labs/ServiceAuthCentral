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
package com.unitvectory.serviceauthcentral.server.token.service;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.unitvectory.consistgen.epoch.EpochTimeProvider;
import com.unitvectory.consistgen.uuid.UuidGenerator;
import com.unitvectory.serviceauthcentral.datamodel.model.Authorization;
import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientJwtBearer;
import com.unitvectory.serviceauthcentral.datamodel.model.LoginCode;
import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.LoginCodeRepository;
import com.unitvectory.serviceauthcentral.server.token.dto.TokenRequest;
import com.unitvectory.serviceauthcentral.server.token.dto.TokenResponse;
import com.unitvectory.serviceauthcentral.server.token.model.JwtBuilder;
import com.unitvectory.serviceauthcentral.sign.service.SignService;
import com.unitvectory.serviceauthcentral.util.HashingUtil;
import com.unitvectory.serviceauthcentral.util.exception.BadRequestException;
import com.unitvectory.serviceauthcentral.util.exception.ForbiddenException;
import com.unitvectory.serviceauthcentral.util.exception.InternalServerErrorException;
import com.unitvectory.serviceauthcentral.util.exception.UnauthorizedException;
import com.unitvectory.serviceauthcentral.verify.model.VerifyJwk;
import com.unitvectory.serviceauthcentral.verify.model.VerifyJwt;
import com.unitvectory.serviceauthcentral.verify.model.VerifyParameters;
import com.unitvectory.serviceauthcentral.verify.service.JwtVerifier;

/**
 * The token service implementing the token exchange logic.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Service
public class TokenService {

	@Value("${sac.issuer}")
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
	private EpochTimeProvider epochTimeProvider;

	@Autowired
	private UuidGenerator uuidGenerator;

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

		long now = this.epochTimeProvider.epochTimeSeconds();

		if (request.getClient_secret() != null) {
			throw new BadRequestException("The request has unexpected parameter 'client_secret'.");
		} else if (request.getAssertion() != null) {
			throw new BadRequestException("The request has unexpected parameter 'assertion'.");
		}

		String code = request.getCode();
		String redirectUri = request.getRedirect_uri();
		String clientId = request.getClient_id();
		String codeVerifier = request.getCode_verifier();
		String audience = request.getAudience();
		String scope = request.getScope();

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
		String codeChallenge = HashingUtil.sha256Base64(codeVerifier);
		if (!codeChallenge.equals(loginCode.getCodeChallenge())) {
			throw new BadRequestException("The request has invalid parameter 'code_verifier'.");
		}

		// Validate the redirect_uri
		if (!redirectUri.equals(loginCode.getRedirectUri())) {
			throw new BadRequestException("The request has invalid parameter 'redirect_uri'.");
		}

		// Validate that the auth code is not expired.
		if (loginCode.getTimeToLive() < now) {
			throw new BadRequestException("The request auth code has expired.");
		}

		Client userClient = this.clientRepository.getClient(loginCode.getUserClientId());

		// Now delete the auth code so it can't be used again
		this.loginCodeRepository.deleteCode(code);

		Client audienceRecord = null;
		Authorization authorizationRecord = null;

		if (audience != null) {
			// Get the audience record
			audienceRecord = clientRepository.getClient(audience);
			if (audienceRecord == null) {
				throw new ForbiddenException("The specified 'audience' is invalid.");
			}

			// Validated the authorization
			authorizationRecord = this.authorizationRepository
					.getAuthorization(loginCode.getUserClientId(), audience);
			if (authorizationRecord == null) {
				throw new ForbiddenException(
						"The client is not authorized for the specified audience.");
			}
		}

		// Get the scopes, this request is permissive, if an invalid or unauthorized
		// scope is passed it just won't be used instead of causing the request to fail
		Set<String> scopeSet = getScopes(scope, audienceRecord, authorizationRecord, true);

		// The description is populated for authorization_code tokens as it identifies
		// the user in a way that can be displayed to the user.
		String description = userClient.getDescription();

		// Build the JWT and return it
		return buildToken(userClient, audienceRecord, authorizationRecord, scopeSet, description);
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
		String scope = request.getScope();

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

		// If the JWK is null, then the JWT is invalid as it could not be retrieved
		if (jwk == null) {
			throw new ForbiddenException("The JWK could not be retrieved. The JWKS or KID is likely invalid.");
		}

		// The parameters in the JWT that will be verified
		VerifyParameters verifyParameters = VerifyParameters.builder().iss(jwtMatchedBearer.getIss())
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
		Authorization authorizationRecord = this.authorizationRepository.getAuthorization(clientId, audience);
		if (authorizationRecord == null) {
			throw new ForbiddenException(
					"The client is not authorized for the specified audience.");
		}

		Set<String> scopeSet = getScopes(scope, audienceRecord, authorizationRecord, false);

		// The description is not being populated for jwt-bearer tokens
		String description = null;

		// Build the JWT and return it
		return buildToken(subjectRecord, audienceRecord, authorizationRecord, scopeSet, description);
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
		String scope = request.getScope();

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
		Authorization authorizationRecord = this.authorizationRepository.getAuthorization(clientId, audience);
		if (authorizationRecord == null) {
			throw new ForbiddenException(
					"The client is not authorized for the specified audience.");
		}

		Set<String> scopeSet = getScopes(scope, audienceRecord, authorizationRecord, false);

		// The description is not being populated for client_credentials tokens
		String description = null;

		// Build the JWT and return it
		return buildToken(subjectRecord, audienceRecord, authorizationRecord, scopeSet, description);
	}

	private Set<String> getScopes(String scope, Client audienceRecord,
			Authorization authorizationRecord, boolean permissive) {
		if (scope == null) {
			return null;
		}

		Set<String> requestedScopes = new TreeSet<>();
		requestedScopes.addAll(Arrays.asList(scope.split(" ")));

		Set<String> validatedScopes = new TreeSet<>();
		for (String s : requestedScopes) {
			if (audienceRecord.hasScope(s) && authorizationRecord.hasScope(s)) {
				validatedScopes.add(s);
			} else if (!permissive) {
				// Permissive scopes will just be ignored; this is used in the case of auth
				// code, but the service aide use cases for client credentials and jwt bearer
				// this will fail hard
				throw new ForbiddenException("The specified scope '" + s + "' is invalid.");
			}
		}

		return validatedScopes;
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
			Authorization authorizationRecord, Set<String> scopes, String description) {

		long now = this.epochTimeProvider.epochTimeSeconds();

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
		builder.withTiming(now, validSeconds);
		builder.withJwtId(this.uuidGenerator.generateUuid());
		builder.withKeyId(kid);
		builder.withDescription(description);

		if (scopes != null && scopes.size() > 0) {
			builder.withScopes(scopes);
		}

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
