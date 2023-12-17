package com.unitvectory.serviceauthcentral.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unitvectory.serviceauthcentral.dto.JwksResponse;
import com.unitvectory.serviceauthcentral.service.signkey.SignKeyService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class JwksController {

	@Autowired
	private SignKeyService keyService;

	@GetMapping("/.well-known/jwks.json")
	public JwksResponse jwks() {
		return JwksResponse.builder().keys(this.keyService.getAllKeys()).build();
	}
}
