package com.unitvectory.auth.server.token.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.cloud.firestore.Firestore;
import com.unitvectory.auth.datamodel.gcp.repository.FirestoreAuthorizationRepository;
import com.unitvectory.auth.datamodel.gcp.repository.FirestoreClientRepository;
import com.unitvectory.auth.datamodel.gcp.repository.FirestoreJwkCacheRepository;
import com.unitvectory.auth.datamodel.repository.AuthorizationRepository;
import com.unitvectory.auth.datamodel.repository.ClientRepository;
import com.unitvectory.auth.datamodel.repository.JwkCacheRepository;
import com.unitvectory.auth.server.token.service.entropy.EntropyService;
import com.unitvectory.auth.server.token.service.entropy.SystemEntropyService;
import com.unitvectory.auth.server.token.service.jwk.CachedJwkService;
import com.unitvectory.auth.server.token.service.jwk.JwksService;
import com.unitvectory.auth.server.token.service.jwk.RemoteJwksService;
import com.unitvectory.auth.server.token.service.signkey.CloudSignKeyService;
import com.unitvectory.auth.server.token.service.signkey.SignKeyService;
import com.unitvectory.auth.server.token.service.time.SystemTimeService;
import com.unitvectory.auth.server.token.service.time.TimeService;

@Configuration
@Profile("!test")
public class ServiceAuthCentralConfig {

	@Autowired
	private Firestore firestore;

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
	public JwkCacheRepository jwkCacheRepository() {
		return new FirestoreJwkCacheRepository(this.firestore);
	}

	@Bean
	public JwksService jwksService() {
		return new CachedJwkService(new RemoteJwksService());
	}
}
