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
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.unitvectory.serviceauthcentral.server.token.dto.JwkResponse;
import com.unitvectory.serviceauthcentral.server.token.dto.JwksResponse;
import com.unitvectory.serviceauthcentral.server.token.mapper.JwkMapper;
import com.unitvectory.serviceauthcentral.server.token.model.JwkETagResponse;
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

	@Value("${sac.server.token.external.cache.seconds:3600}")
	private long externalCacheHours;

	@GetMapping("/.well-known/jwks.json")
	public ResponseEntity<JwksResponse> jwks(WebRequest request) {

		JwkETagResponse jwksETagResponse = this.getJwks();
		String eTag = jwksETagResponse.getETag();

		if (request.checkNotModified(eTag)) {
			// Return 304 if not modified, the ETag is the same
			return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
		}

		// Return the response with the ETag
		return ResponseEntity
				.ok()
                .contentType(MediaType.APPLICATION_JSON)
				// This response can be cached for the externalCacheHours, goal is to reduce the number of calls to the Sign service
				.cacheControl(CacheControl.maxAge(this.externalCacheHours, TimeUnit.SECONDS))
				.eTag(eTag)
				.body(jwksETagResponse.getJwks());
	}

	@Cacheable(value = "jwksCache", key = "'jwksKey'")
	private JwkETagResponse getJwks() {
		// This requires a call to the Sign service which is an external call so we want to cache the result to reduce the number of calls
		List<SignJwk> key = this.signService.getAll();

		List<JwkResponse> keys = new ArrayList<>();

		for (SignJwk k : key) {
			keys.add(JwkMapper.INSTANCE.signJwkToJwkResponse(k));
		}

		return new JwkETagResponse(JwksResponse.builder().keys(keys).build());
	}
}
