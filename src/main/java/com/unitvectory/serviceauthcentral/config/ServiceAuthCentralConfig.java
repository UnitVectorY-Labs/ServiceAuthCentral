package com.unitvectory.serviceauthcentral.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.unitvectory.serviceauthcentral.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.repository.FirestoreClientRepository;
import com.unitvectory.serviceauthcentral.repository.FirestoreKeySetRepository;
import com.unitvectory.serviceauthcentral.repository.KeySetRepository;
import com.unitvectory.serviceauthcentral.service.CloudKeyService;
import com.unitvectory.serviceauthcentral.service.EntropyService;
import com.unitvectory.serviceauthcentral.service.KeyService;
import com.unitvectory.serviceauthcentral.service.SystemEntropyService;
import com.unitvectory.serviceauthcentral.service.SystemTimeService;
import com.unitvectory.serviceauthcentral.service.TimeService;

@Configuration
@Profile("!test")
public class ServiceAuthCentralConfig {

	@Bean
	public TimeService timeService() {
		return new SystemTimeService();
	}

	@Bean
	public EntropyService entropyService() {
		return new SystemEntropyService();
	}

	@Bean
	public KeyService keyService() {
		return new CloudKeyService();
	}

	@Bean
	public ClientRepository clientRepository() {
		return new FirestoreClientRepository();
	}

	@Bean
	public KeySetRepository keySetRepository() {
		return new FirestoreKeySetRepository();
	}
}
