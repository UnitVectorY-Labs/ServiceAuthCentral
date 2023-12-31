package com.unitvectory.auth.server.token.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unitvectory.auth.server.token.dto.TokenRequest;
import com.unitvectory.auth.server.token.dto.TokenResponse;
import com.unitvectory.auth.server.token.service.TokenService;
import com.unitvectory.auth.util.exception.BadRequestException;

import jakarta.validation.Valid;

@RestController
public class TokenController {

	@Autowired
	private TokenService tokenService;

	@PostMapping(path = "/v1/token", consumes = "application/x-www-form-urlencoded")
	public ResponseEntity<TokenResponse> token(@Valid TokenRequest tokenRequest, BindingResult result)
			throws Exception {
		if (result.hasErrors()) {
			// Throw custom exception
			// throw new BadRequestException(result);
			throw new BadRequestException();
		}

		String grantType = tokenRequest.getGrant_type();
		if ("client_credentials".equals(grantType)) {
			TokenResponse tokenResponse = this.tokenService.clientCredentials(tokenRequest);
			return ResponseEntity.ok(tokenResponse);
		} else if ("urn:ietf:params:oauth:grant-type:jwt-bearer".equals(grantType)) {
			TokenResponse tokenResponse = this.tokenService.jwtAssertion(tokenRequest);
			return ResponseEntity.ok(tokenResponse);
		} else {
			// Invalid value for the grant type
			throw new BadRequestException("The request 'grant_type' provided is not supported.");
		}

	}
}
