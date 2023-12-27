package com.unitvectory.serviceauthcentral.manage.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.cloud.firestore.Firestore;
import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.gcp.repository.FirestoreAuthorizationRepository;
import com.unitvectory.serviceauthcentral.gcp.repository.FirestoreClientRepository;
import com.unitvectory.serviceauthcentral.manage.entropy.EntropyService;
import com.unitvectory.serviceauthcentral.manage.entropy.SystemEntropyService;
import com.unitvectory.serviceauthcentral.manage.service.AuthorizationService;
import com.unitvectory.serviceauthcentral.manage.service.ClientService;
import com.unitvectory.serviceauthcentral.manage.service.DefaultAuthorizationService;
import com.unitvectory.serviceauthcentral.manage.service.DefaultClientService;

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
