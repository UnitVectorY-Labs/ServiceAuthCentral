package com.unitvectory.auth.server.token.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import com.unitvectory.auth.common.entropy.EntropyService;
import com.unitvectory.auth.common.entropy.StaticEntropyService;
import com.unitvectory.auth.common.time.StaticTimeService;
import com.unitvectory.auth.common.time.TimeService;
import com.unitvectory.auth.datamodel.memory.repository.MemoryAuthorizationRepository;
import com.unitvectory.auth.datamodel.memory.repository.MemoryClientRepository;
import com.unitvectory.auth.datamodel.memory.repository.MemoryJwkCacheRepository;
import com.unitvectory.auth.datamodel.repository.AuthorizationRepository;
import com.unitvectory.auth.datamodel.repository.ClientRepository;
import com.unitvectory.auth.datamodel.repository.JwkCacheRepository;
import com.unitvectory.auth.server.token.service.jwk.JwksService;
import com.unitvectory.auth.server.token.service.jwk.MockedJwksService;

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
	public JwksService jwksService() {
		return new MockedJwksService();
	}
}
