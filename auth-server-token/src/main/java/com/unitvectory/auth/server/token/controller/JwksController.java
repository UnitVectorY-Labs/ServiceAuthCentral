package com.unitvectory.auth.server.token.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unitvectory.auth.server.token.dto.JwksResponse;
import com.unitvectory.auth.server.token.service.signkey.SignKeyService;

@RestController
public class JwksController {

	@Autowired
	private SignKeyService keyService;

	@GetMapping("/.well-known/jwks.json")
	public JwksResponse jwks() {
		return JwksResponse.builder().keys(this.keyService.getAllKeys()).build();
	}
}
