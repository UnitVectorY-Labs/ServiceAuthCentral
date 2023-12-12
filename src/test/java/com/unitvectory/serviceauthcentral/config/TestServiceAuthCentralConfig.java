package com.unitvectory.serviceauthcentral.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import com.unitvectory.serviceauthcentral.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.service.EntropyService;
import com.unitvectory.serviceauthcentral.service.KeyService;
import com.unitvectory.serviceauthcentral.service.MokedClientRepository;
import com.unitvectory.serviceauthcentral.service.MokedKeyService;
import com.unitvectory.serviceauthcentral.service.StaticEntropyService;
import com.unitvectory.serviceauthcentral.service.StaticTimeService;
import com.unitvectory.serviceauthcentral.service.TimeService;

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
	public ClientRepository clientRepository() {
		return new MokedClientRepository();
	}
}
