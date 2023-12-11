package com.unitvectory.serviceauthcentral.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unitvectory.serviceauthcentral.dto.TokenRequest;
import com.unitvectory.serviceauthcentral.dto.TokenResponse;
import com.unitvectory.serviceauthcentral.service.TokenService;

@RestController
public class TokenController {

	@Autowired
	private TokenService tokenService;

	@PostMapping(path = "/v1/token", consumes = "application/x-www-form-urlencoded")
	public ResponseEntity<TokenResponse> token(TokenRequest tokenRequest) throws Exception {

		TokenResponse tokenResponse = this.tokenService.token(tokenRequest);
		return ResponseEntity.ok(tokenResponse);
	}
}
