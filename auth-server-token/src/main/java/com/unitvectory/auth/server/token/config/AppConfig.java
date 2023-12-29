package com.unitvectory.auth.server.token.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
public class AppConfig {

	@Value("${serviceauthcentral.cache.jwks.hours:1}")
	private int cacheJwksHours;

	@Value("${serviceauthcentral.key.location}")
	private String keyLocation;

	@Value("${serviceauthcentral.key.ring}")
	private String keyRing;

	@Value("${serviceauthcentral.key.name}")
	private String keyName;

	@Value("${serviceauthcentral.jwt.issuer}")
	private String jwtIssuer;
}
