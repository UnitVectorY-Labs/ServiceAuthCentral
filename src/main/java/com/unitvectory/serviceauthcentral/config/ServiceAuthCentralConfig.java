package com.unitvectory.serviceauthcentral.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.unitvectory.serviceauthcentral.repository.authorization.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.repository.authorization.FirestoreAuthorizationRepository;
import com.unitvectory.serviceauthcentral.repository.client.ClientRepository;
import com.unitvectory.serviceauthcentral.repository.client.FirestoreClientRepository;
import com.unitvectory.serviceauthcentral.repository.key.FirestoreKeySetRepository;
import com.unitvectory.serviceauthcentral.repository.key.KeySetRepository;
import com.unitvectory.serviceauthcentral.service.entropy.EntropyService;
import com.unitvectory.serviceauthcentral.service.entropy.SystemEntropyService;
import com.unitvectory.serviceauthcentral.service.key.CloudKeyService;
import com.unitvectory.serviceauthcentral.service.key.KeyService;
import com.unitvectory.serviceauthcentral.service.time.SystemTimeService;
import com.unitvectory.serviceauthcentral.service.time.TimeService;

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
	public AuthorizationRepository authorizationRepository() {
		return new FirestoreAuthorizationRepository();
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
