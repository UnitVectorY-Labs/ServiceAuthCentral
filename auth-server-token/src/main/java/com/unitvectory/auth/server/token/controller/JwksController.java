package com.unitvectory.auth.server.token.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unitvectory.auth.server.token.dto.JwksKey;
import com.unitvectory.auth.server.token.dto.JwksResponse;
import com.unitvectory.auth.server.token.mapper.JwksKeyMapper;
import com.unitvectory.auth.sign.model.JsonWebKey;
import com.unitvectory.auth.sign.service.SignService;

@RestController
public class JwksController {

	@Autowired
	private SignService signService;

	@GetMapping("/.well-known/jwks.json")
	public JwksResponse jwks() {
		List<JsonWebKey> key = this.signService.getAll();

		List<JwksKey> keys = new ArrayList<>();

		for (JsonWebKey k : key) {
			keys.add(JwksKeyMapper.INSTANCE.jsonWebKeyTojwksKey(k));
		}

		return JwksResponse.builder().keys(keys).build();
	}
}
