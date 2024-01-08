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
import com.unitvectory.auth.verify.model.VerifyJwk;
import com.unitvectory.auth.verify.model.VerifyJwks;
import com.unitvectory.auth.verify.model.VerifyJwt;
import com.unitvectory.auth.verify.model.VerifyParameters;
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
		// TODO: Return a better mocked implementation for local testing
		return new JwksResolver() {
			@Override
			public VerifyJwks getJwks(String url) {
				return null;
			}
		};
	}

	@Bean
	public JwtVerifier jwtVerifier() {
		// TODO: Return a better mocked implementation for local testing
		return new JwtVerifier() {
			@Override
			public VerifyJwt extractClaims(String token) {
				return null;
			}

			@Override
			public boolean verifySignature(String token, VerifyJwk jwk, VerifyParameters verifyParameters) {
				return false;
			}
		};
	}
}
