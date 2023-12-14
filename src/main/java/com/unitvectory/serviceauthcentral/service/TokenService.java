package com.unitvectory.serviceauthcentral.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unitvectory.serviceauthcentral.dto.TokenRequest;
import com.unitvectory.serviceauthcentral.dto.TokenResponse;
import com.unitvectory.serviceauthcentral.exception.BadRequestException;
import com.unitvectory.serviceauthcentral.exception.ForbiddenException;
import com.unitvectory.serviceauthcentral.exception.InternalServerErrorException;
import com.unitvectory.serviceauthcentral.exception.UnauthorizedException;
import com.unitvectory.serviceauthcentral.model.AuthorizationRecord;
import com.unitvectory.serviceauthcentral.model.ClientRecord;
import com.unitvectory.serviceauthcentral.repository.ClientRepository;

@Service
public class TokenService {

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private CryptoService cryptoService;

	@Autowired
	private KeyService keyService;

	private TokenResponse buildToken(ClientRecord subjectRecord, ClientRecord audienceRecord,
			AuthorizationRecord authorizationRecord) {

		String clientId = subjectRecord.getClientId();
		String audience = audienceRecord.getClientId();

		// Get the active key
		String keyName = keyService.getActiveKeyName();
		if (keyName == null) {
			throw new InternalServerErrorException("No active signing keys, cannot generate access token.");
		}

		// How long the JWT is valid
		long validSeconds = 3600;

		// Generate the unsigned JWT
		String unsignedJwt = cryptoService.buildUnsignedJwt(keyName, clientId, audience, validSeconds);

		// Sign the JWT
		String jwt = keyService.signJwt(keyName, unsignedJwt);

		// Build the response
		return TokenResponse.builder().withAccess_token(jwt).withExpires_in(validSeconds).withToken_type("Bearer")
				.build();
	}

	public TokenResponse jwtAssertion(TokenRequest request) throws Exception {

		String clientId = request.getClient_id();
		String audience = request.getAudience();

		String assertion = request.getAssertion();
		if (assertion == null || assertion.isEmpty()) {
			throw new BadRequestException("The request is missing the required parameter 'assertion'.");
		}

		if (request.getClient_secret() != null) {
			throw new BadRequestException("The request has unexpected parameter 'client_secret'.");
		}

		// Get the subject record
		ClientRecord subjectRecord = clientRepository.getClient(clientId);
		if (subjectRecord == null) {
			throw new UnauthorizedException("The request has invalid 'client_id'.");
		}

		// TODO: Verify the assertion

		// Get the audience record
		ClientRecord audienceRecord = clientRepository.getClient(audience);
		if (audienceRecord == null) {
			throw new ForbiddenException("The specified 'audience' is invalid.");
		}

		// Validated the authorization
		AuthorizationRecord authorizationRecord = clientRepository.getAuthorization(clientId, audience);
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
		ClientRecord subjectRecord = clientRepository.getClient(clientId);
		if (subjectRecord == null) {
			throw new UnauthorizedException("The request has invalid 'client_id'.");
		}

		// Verify the client_secret
		if (!subjectRecord.verifySecret(clientSecret)) {
			throw new UnauthorizedException("The request has invalid 'client_id' or 'client_secret.");
		}

		// Get the audience record
		ClientRecord audienceRecord = clientRepository.getClient(audience);
		if (audienceRecord == null) {
			throw new ForbiddenException("The specified 'audience' is invalid.");
		}

		// Validated the authorization
		AuthorizationRecord authorizationRecord = clientRepository.getAuthorization(clientId, audience);
		if (authorizationRecord == null) {
			throw new ForbiddenException("The specified 'audience' is invalid.");
		}

		// Build the JWT and return it
		return buildToken(subjectRecord, audienceRecord, authorizationRecord);
	}
}
