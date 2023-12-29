package com.unitvectory.serviceauthcentral.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.JwkCacheRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.MemoryAuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.MemoryClientRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.MemoryJwkCacheRepository;
import com.unitvectory.serviceauthcentral.service.MokedKeyService;
import com.unitvectory.serviceauthcentral.service.entropy.EntropyService;
import com.unitvectory.serviceauthcentral.service.entropy.StaticEntropyService;
import com.unitvectory.serviceauthcentral.service.jwk.JwksService;
import com.unitvectory.serviceauthcentral.service.jwk.MockedJwksService;
import com.unitvectory.serviceauthcentral.service.signkey.SignKeyService;
import com.unitvectory.serviceauthcentral.service.time.StaticTimeService;
import com.unitvectory.serviceauthcentral.service.time.TimeService;

@TestConfiguration
@Profile("test")
public class TestServiceAuthCentralConfig {

	@Bean
	public AppConfig appConfig() {
		return new AppConfig();
	}

	@Bean
	public TimeService timeService() {
		return new StaticTimeService();
	}

	@Bean
	public EntropyService entropyService() {
		return new StaticEntropyService();
	}

	@Bean
	public SignKeyService keyService() {
		return new MokedKeyService();
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
