package com.unitvectory.serviceauthcentral.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import com.unitvectory.serviceauthcentral.repository.authorization.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.repository.authorization.MockedAuthorizationRepository;
import com.unitvectory.serviceauthcentral.repository.client.ClientRepository;
import com.unitvectory.serviceauthcentral.repository.client.MokedClientRepository;
import com.unitvectory.serviceauthcentral.repository.key.KeySetRepository;
import com.unitvectory.serviceauthcentral.repository.key.MockedKeySetRepository;
import com.unitvectory.serviceauthcentral.service.MokedKeyService;
import com.unitvectory.serviceauthcentral.service.entropy.EntropyService;
import com.unitvectory.serviceauthcentral.service.entropy.StaticEntropyService;
import com.unitvectory.serviceauthcentral.service.key.KeyService;
import com.unitvectory.serviceauthcentral.service.time.StaticTimeService;
import com.unitvectory.serviceauthcentral.service.time.TimeService;

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
	public KeyService keyService() {
		return new MokedKeyService();
	}

	@Bean
	public AuthorizationRepository authorizationRepository() {
		return new MockedAuthorizationRepository();
	}

	@Bean
	public ClientRepository clientRepository() {
		return new MokedClientRepository();
	}

	@Bean
	public KeySetRepository keySetRepository() {
		return new MockedKeySetRepository();
	}
}
