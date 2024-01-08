package com.unitvectory.auth.verify.auth0.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.unitvectory.auth.verify.auth0.service.Auth0JwksResolver;
import com.unitvectory.auth.verify.auth0.service.Auth0JwtVerifier;
import com.unitvectory.auth.verify.service.JwksResolver;
import com.unitvectory.auth.verify.service.JwtVerifier;

@Configuration
@Profile("verify-auth0")
public class VerifyAuth0Config {

	@Bean
	public JwksResolver jwksResolver() {
		return new Auth0JwksResolver();
	}

	@Bean
	public JwtVerifier jwtVerifier() {
		return new Auth0JwtVerifier();
	}
}
