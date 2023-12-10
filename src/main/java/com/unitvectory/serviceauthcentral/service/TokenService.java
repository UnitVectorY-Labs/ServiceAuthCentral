package com.unitvectory.serviceauthcentral.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unitvectory.serviceauthcentral.dto.TokenRequest;
import com.unitvectory.serviceauthcentral.dto.TokenResponse;

@Service
public class TokenService {

	@Autowired
	private CryptoService cryptoService;

	@Autowired
	private KeyService keyService;

	public TokenResponse token(TokenRequest request) {

		String keyName = keyService.getActiveKeyName();

		// TODO: Do the actual request input validation,

		// TODO: check the database to make sure the request is authorized

		String subject = request.getClient_id();
		String audience = request.getAudience();

		long validSeconds = 3600;

		String unsignedJwt = cryptoService.buildUnsignedJwt(keyName, subject, audience, validSeconds);

		String jwt = keyService.signJwt(keyName, unsignedJwt);

		return TokenResponse.builder().withAccess_token(jwt).withExpires_in(validSeconds).withToken_type("Bearer")
				.build();
	}
}
