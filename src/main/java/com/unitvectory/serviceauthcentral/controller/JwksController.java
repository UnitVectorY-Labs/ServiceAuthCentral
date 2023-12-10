package com.unitvectory.serviceauthcentral.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unitvectory.serviceauthcentral.dto.JwksKey;
import com.unitvectory.serviceauthcentral.dto.JwksResponse;

@RestController
public class JwksController {

	@GetMapping("/v1/jwks")
	public JwksResponse jwks() {

		// TODO: Load in the JWKS keys
		List<JwksKey> keys = new ArrayList<>();
		return JwksResponse.builder().keys(keys).build();
	}
}
