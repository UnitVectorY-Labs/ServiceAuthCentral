package com.unitvectory.auth.server.manage.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.cloud.firestore.Firestore;
import com.unitvectory.auth.datamodel.gcp.repository.FirestoreAuthorizationRepository;
import com.unitvectory.auth.datamodel.gcp.repository.FirestoreClientRepository;
import com.unitvectory.auth.datamodel.repository.AuthorizationRepository;
import com.unitvectory.auth.datamodel.repository.ClientRepository;
import com.unitvectory.auth.server.manage.service.AuthorizationService;
import com.unitvectory.auth.server.manage.service.ClientService;
import com.unitvectory.auth.server.manage.service.DefaultAuthorizationService;
import com.unitvectory.auth.server.manage.service.DefaultClientService;
import com.unitvectory.auth.server.manage.service.entropy.EntropyService;
import com.unitvectory.auth.server.manage.service.entropy.SystemEntropyService;

@Configuration
public class ServiceAuthCentralConfig {

	@Autowired
	private Firestore firestore;

	@Bean
	public AuthorizationRepository authorizationRepository() {
		return new FirestoreAuthorizationRepository(this.firestore);
	}

	@Bean
	public ClientRepository clientRepository() {
		return new FirestoreClientRepository(this.firestore);
	}

	@Bean
	public EntropyService entropyService() {
		return new SystemEntropyService();
	}

	@Bean
	public ClientService clientService() {
		return new DefaultClientService();
	}

	@Bean
	AuthorizationService authorizationService() {
		return new DefaultAuthorizationService();
	}
}
