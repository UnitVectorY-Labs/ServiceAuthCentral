package com.unitvectory.serviceauthcentral.manage.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.cloud.firestore.Firestore;
import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.gcp.repository.FirestoreAuthorizationRepository;
import com.unitvectory.serviceauthcentral.gcp.repository.FirestoreClientRepository;

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

}
