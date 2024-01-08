package com.unitvectory.auth.server.token.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import com.unitvectory.auth.common.service.entropy.EntropyService;
import com.unitvectory.auth.common.service.entropy.StaticEntropyService;
import com.unitvectory.auth.common.service.time.StaticTimeService;
import com.unitvectory.auth.common.service.time.TimeService;
import com.unitvectory.auth.datamodel.memory.repository.MemoryAuthorizationRepository;
import com.unitvectory.auth.datamodel.memory.repository.MemoryClientRepository;
import com.unitvectory.auth.datamodel.memory.repository.MemoryJwkCacheRepository;
import com.unitvectory.auth.datamodel.repository.AuthorizationRepository;
import com.unitvectory.auth.datamodel.repository.ClientRepository;
import com.unitvectory.auth.datamodel.repository.JwkCacheRepository;
import com.unitvectory.auth.server.token.service.LocalJwksResolver;
import com.unitvectory.auth.verify.auth0.service.Auth0JwtVerifier;
import com.unitvectory.auth.verify.service.JwksResolver;
import com.unitvectory.auth.verify.service.JwtVerifier;

@TestConfiguration
@Profile("test")
public class TestServiceAuthCentralConfig {

	@Bean
	public TimeService timeService() {
		return new StaticTimeService();
	}

	@Bean
	public EntropyService entropyService() {
		return new StaticEntropyService();
	}

	@Bean
	public AuthorizationRepository authorizationRepository() {
		return new MemoryAuthorizationRepository();
	}

	@Bean
	public ClientRepository clientRepository() {
		return new MemoryClientRepository();
	}

	@Bean
	public JwkCacheRepository jwkCacheRepository() {
		return new MemoryJwkCacheRepository();
	}

	@Bean
	public JwksResolver jwksResolver() {
		return new LocalJwksResolver();
	}

	@Bean
	public JwtVerifier jwtVerifier() {
		// This implementation works for testing
		return new Auth0JwtVerifier();
	}
}
