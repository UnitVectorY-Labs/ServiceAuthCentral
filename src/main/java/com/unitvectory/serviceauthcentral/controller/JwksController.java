package com.unitvectory.serviceauthcentral.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unitvectory.serviceauthcentral.dto.JwksResponse;
import com.unitvectory.serviceauthcentral.service.KeyService;

@RestController
public class JwksController {

	@Autowired
	private KeyService keyService;

	@GetMapping("/v1/jwks")
	public JwksResponse jwks() {
		return JwksResponse.builder().keys(this.keyService.getAllKeys()).build();
	}
}
