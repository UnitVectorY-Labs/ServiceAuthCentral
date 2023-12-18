package com.unitvectory.serviceauthcentral.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.unitvectory.serviceauthcentral.dto.TokenRequest;
import com.unitvectory.serviceauthcentral.dto.TokenResponse;
import com.unitvectory.serviceauthcentral.exception.BadRequestException;
import com.unitvectory.serviceauthcentral.service.TokenService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = RequestMethod.POST)
public class TokenController {

	@Autowired
	private TokenService tokenService;

	@RequestMapping(value = "/v1/token", method = RequestMethod.OPTIONS)
	public void corsHeaders(HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
		response.addHeader("Access-Control-Allow-Headers",
				"Origin, X-Requested-With, Content-Type, Accept, Authorization");
		response.addHeader("Access-Control-Max-Age", "3600");
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@PostMapping(path = "/v1/token", consumes = "application/x-www-form-urlencoded")
	public ResponseEntity<TokenResponse> token(@Valid TokenRequest tokenRequest, BindingResult result)
			throws Exception {
		if (result.hasErrors()) {
			// Throw custom exception
			throw new BadRequestException(result);
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
