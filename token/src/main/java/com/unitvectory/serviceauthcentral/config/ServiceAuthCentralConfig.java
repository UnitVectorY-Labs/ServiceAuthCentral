package com.unitvectory.serviceauthcentral.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.cloud.firestore.Firestore;
import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.gcp.repository.FirestoreAuthorizationRepository;
import com.unitvectory.serviceauthcentral.gcp.repository.FirestoreClientRepository;
import com.unitvectory.serviceauthcentral.repository.key.FirestoreKeySetRepository;
import com.unitvectory.serviceauthcentral.repository.key.KeySetRepository;
import com.unitvectory.serviceauthcentral.service.entropy.EntropyService;
import com.unitvectory.serviceauthcentral.service.entropy.SystemEntropyService;
import com.unitvectory.serviceauthcentral.service.jwk.CachedJwkService;
import com.unitvectory.serviceauthcentral.service.jwk.JwksService;
import com.unitvectory.serviceauthcentral.service.jwk.RemoteJwksService;
import com.unitvectory.serviceauthcentral.service.signkey.CloudSignKeyService;
import com.unitvectory.serviceauthcentral.service.signkey.SignKeyService;
import com.unitvectory.serviceauthcentral.service.time.SystemTimeService;
import com.unitvectory.serviceauthcentral.service.time.TimeService;

@Configuration
@Profile("!test")
public class ServiceAuthCentralConfig {

	@Autowired
	private Firestore firestore;

	@Bean
	public AppConfig appConfig() {
		return new AppConfig();
	}

	@Bean
	public TimeService timeService() {
		return new SystemTimeService();
	}

	@Bean
	public EntropyService entropyService() {
		return new SystemEntropyService();
	}

	@Bean
	public SignKeyService keyService() {
		return new CloudSignKeyService();
	}

	@Bean
	public AuthorizationRepository authorizationRepository() {
		return new FirestoreAuthorizationRepository(this.firestore);
	}

	@Bean
	public ClientRepository clientRepository() {
		return new FirestoreClientRepository(this.firestore);
	}

	@Bean
	public KeySetRepository keySetRepository() {
		return new FirestoreKeySetRepository();
	}

	@Bean
	public JwksService jwksService() {
		return new CachedJwkService(new RemoteJwksService());
	}
}
