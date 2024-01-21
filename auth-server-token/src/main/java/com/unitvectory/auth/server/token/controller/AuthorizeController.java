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

@RestController
public class AuthorizeController {

	// http://localhost:8082/login/authorize?response_type=code&client_id=provider:github&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fcallback&state=mystate&code_challenge=dab9f87eab32111dcb1aeff65c88fd7ef42ea25ef54e069eceedda749f18fe06&code_challenge_method=S256

	@Autowired
	private LoginService loginService;

	@Autowired
	private EntropyService entropyService;

	@GetMapping("/login/authorize")
	public void authorize(@RequestParam("response_type") String responseType,
			@RequestParam("client_id") String clientId, @RequestParam("redirect_uri") String redirectUri,
			@RequestParam("code_challenge") String codeChallenge,
			@RequestParam("code_challenge_method") String codeChallengeMethod, @RequestParam("state") String state,
			HttpServletResponse response) throws IOException {

		String sessionId = this.entropyService.randomAlphaNumeric(25);

		String redirect = this.loginService.authorize(sessionId, responseType, clientId, redirectUri, codeChallenge,
				codeChallengeMethod, state);

		// Save the cookie
		Cookie cookie = new Cookie("sessionId", sessionId);
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setPath("/login/");
		cookie.setAttribute("SameSite", "Strict");
		cookie.setMaxAge(300);
		response.addCookie(cookie);

		// Perform the redirection
		response.sendRedirect(redirect);
	}

	@GetMapping("/login/callback")
	public void callback(@CookieValue(name = "sessionId", required = true) String sessionId,
			@RequestParam("code") String code, @RequestParam("state") String state, HttpServletResponse response)
			throws IOException {

		String redirect = this.loginService.callback(sessionId, code, state, response);

		// Clear the cookie
		Cookie cookie = new Cookie("sessionId", "");
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setPath("/login/");
		cookie.setAttribute("SameSite", "Strict");
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
