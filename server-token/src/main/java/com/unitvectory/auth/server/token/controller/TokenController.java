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
package com.unitvectory.auth.server.token.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unitvectory.auth.server.token.dto.TokenRequest;
import com.unitvectory.auth.server.token.dto.TokenResponse;
import com.unitvectory.auth.server.token.service.TokenService;
import com.unitvectory.auth.util.exception.BadRequestException;

import jakarta.validation.Valid;

/**
 * The Token Controller
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@RestController
public class TokenController {

	@Autowired
	private TokenService tokenService;

	@PostMapping(path = "/v1/token", consumes = "application/x-www-form-urlencoded")
	public ResponseEntity<TokenResponse> token(@Valid TokenRequest tokenRequest,
			BindingResult result) throws Exception {
		if (result.hasErrors()) {

			// TODO: Ideally multiple messages should be able to be returned via an
			// exception
			StringBuilder sb = new StringBuilder();

			// Multiple errors, include all of them for (ObjectError
			for (ObjectError error : result.getAllErrors()) {
				sb.append(error.getDefaultMessage());
			}

			throw new BadRequestException(sb.toString());
		}

		return ResponseEntity.ok(this.tokenService.token(tokenRequest));
	}
}
