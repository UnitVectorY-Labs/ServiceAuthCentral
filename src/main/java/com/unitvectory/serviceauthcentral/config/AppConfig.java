package com.unitvectory.serviceauthcentral.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
@ConfigurationProperties(prefix = "serviceauthcentral")
public class AppConfig {

	@Value("${cache.jwks.hours}")
	private int cacheJwksHours;

	@Value("${key.location}")
	private String keyLocation;

	@Value("${key.ring}")
	private String keyRing;

	@Value("${key.name}")
	private String keyName;

	@Value("${jwt.issuer}")
	private String jwtIssuer;
}
