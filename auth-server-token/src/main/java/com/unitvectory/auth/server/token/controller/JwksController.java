package com.unitvectory.auth.server.token.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unitvectory.auth.server.token.dto.JwkResponse;
import com.unitvectory.auth.server.token.dto.JwksResponse;
import com.unitvectory.auth.server.token.mapper.JwkMapper;
import com.unitvectory.auth.sign.model.SignJwk;
import com.unitvectory.auth.sign.service.SignService;

@RestController
public class JwksController {

	@Autowired
	private SignService signService;

	@Cacheable(value = "jwksCache", key = "'jwksKey'")
	@GetMapping("/.well-known/jwks.json")
	public JwksResponse jwks() {
		List<SignJwk> key = this.signService.getAll();

		List<JwkResponse> keys = new ArrayList<>();

		for (SignJwk k : key) {
			keys.add(JwkMapper.INSTANCE.signJwkToJwkResponse(k));
		}

		return JwksResponse.builder().keys(keys).build();
	}
}
