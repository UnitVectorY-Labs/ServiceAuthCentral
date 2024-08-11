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

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unitvectory.auth.common.service.entropy.EntropyService;
import com.unitvectory.auth.server.token.service.LoginService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The Authorization Controller
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@RestController
public class AuthorizeController {

	@Autowired
	private LoginService loginService;

	@Autowired
	private EntropyService entropyService;

	@GetMapping("/login/authorize")
	public void authorize(@RequestParam("response_type") String responseType,
			@RequestParam("client_id") String clientId,
			@RequestParam("redirect_uri") String redirectUri,
			@RequestParam("code_challenge") String codeChallenge,
			@RequestParam("code_challenge_method") String codeChallengeMethod,
			@RequestParam("state") String state, HttpServletResponse response) throws IOException {

		String sessionId = this.entropyService.randomAlphaNumeric(25);

		String redirect = this.loginService.authorize(sessionId, responseType, clientId,
				redirectUri, codeChallenge, codeChallengeMethod, state);

		// Save the cookie
		Cookie cookie = new Cookie("sessionId", sessionId);
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		// cookie.setPath("/login/");
		// cookie.setAttribute("SameSite", "Strict");
		cookie.setMaxAge(300);
		response.addCookie(cookie);

		// Perform the redirection
		response.sendRedirect(redirect);
	}

	@GetMapping("/login/callback")
	public void callback(@CookieValue(name = "sessionId", required = true) String sessionId,
			@RequestParam("code") String code, @RequestParam("state") String state,
			HttpServletResponse response) throws IOException {

		String redirect = this.loginService.callback(sessionId, code, state, response);

		// Clear the cookie
		Cookie cookie = new Cookie("sessionId", "");
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		// cookie.setPath("/login/");
		// cookie.setAttribute("SameSite", "Strict");
		cookie.setMaxAge(0);
		response.addCookie(cookie);

		// Perform the redirection
		response.sendRedirect(redirect);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<String> handleMissingParams(MissingServletRequestParameterException ex,
			HttpServletResponse response) {
		// You can use ex.getParameterName() to get the name of the missing parameter
		String paramName = ex.getParameterName();
		String message = String.format("The parameter '%s' is required.", paramName);
		return ResponseEntity.badRequest().body(message);

		// TODO: Goal here is to send redirect if any of the required parameters are
		// missing, they are all required.
	}
}
