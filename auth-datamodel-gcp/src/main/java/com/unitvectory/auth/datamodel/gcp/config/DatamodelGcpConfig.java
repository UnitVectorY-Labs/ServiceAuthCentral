package com.unitvectory.auth.datamodel.gcp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${serviceauthcentral.datamodel.gcp.collection.authorizations:authorizations}")
	private String collectionAuthorizations;

	@Value("${serviceauthcentral.datamodel.gcp.collection.clients:clients}")
	private String collectionClients;

	@Value("${serviceauthcentral.datamodel.gcp.collection.keys:keys}")
	private String collectionKeys;

	@Bean
	public AuthorizationRepository authorizationRepository() {
		return new FirestoreAuthorizationRepository(this.firestore, this.collectionAuthorizations);
	}

	@Bean
	public ClientRepository clientRepository() {
		return new FirestoreClientRepository(this.firestore, this.collectionClients);
	}

	@Bean
	public JwkCacheRepository jwkCacheRepository() {
		return new FirestoreJwkCacheRepository(this.firestore, this.collectionKeys);
	}
}
