package com.unitvectory.auth.datamodel.gcp.config;

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

@Configuration
@Profile("datamodel-gcp")
public class DatamodelGcpConfig {

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
	public JwkCacheRepository jwkCacheRepository() {
		return new FirestoreJwkCacheRepository(this.firestore);
	}
}
