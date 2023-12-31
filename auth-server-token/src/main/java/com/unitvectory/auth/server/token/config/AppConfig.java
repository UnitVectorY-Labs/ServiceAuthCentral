package com.unitvectory.auth.server.token.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
public class AppConfig {

	@Value("${serviceauthcentral.cache.jwks.hours:1}")
	private int cacheJwksHours;

	@Value("${serviceauthcentral.jwt.issuer}")
	private String jwtIssuer;
}
