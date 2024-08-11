/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.unitvectory.serviceauthcentral.server.token.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unitvectory.serviceauthcentral.server.token.dto.JwkResponse;
import com.unitvectory.serviceauthcentral.server.token.dto.JwksResponse;
import com.unitvectory.serviceauthcentral.server.token.mapper.JwkMapper;
import com.unitvectory.serviceauthcentral.sign.model.SignJwk;
import com.unitvectory.serviceauthcentral.sign.service.SignService;

/**
 * The JWKS Controller
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
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
