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
import com.unitvectory.auth.datamodel.gcp.repository.FirestoreLoginCodeRepository;
import com.unitvectory.auth.datamodel.gcp.repository.FirestoreLoginStateRepository;
import com.unitvectory.auth.datamodel.repository.AuthorizationRepository;
import com.unitvectory.auth.datamodel.repository.ClientRepository;
import com.unitvectory.auth.datamodel.repository.JwkCacheRepository;
import com.unitvectory.auth.datamodel.repository.LoginCodeRepository;
import com.unitvectory.auth.datamodel.repository.LoginStateRepository;

@Configuration
@Profile("datamodel-gcp")
public class DatamodelGcpConfig {

	@Autowired
	private Firestore firestore;

	@Value("${sac.datamodel.gcp.collection.authorizations:authorizations}")
	private String collectionAuthorizations;

	@Value("${sac.datamodel.gcp.collection.clients:clients}")
	private String collectionClients;

	@Value("${sac.datamodel.gcp.collection.keys:keys}")
	private String collectionKeys;

	@Value("${sac.datamodel.gcp.collection.logincodes:loginCodes}")
	private String collectionLoginCodes;

	@Value("${sac.datamodel.gcp.collection.loginstates:loginStates}")
	private String collectionLoginStates;

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

	@Bean
	public LoginCodeRepository loginCodeRepository() {
		return new FirestoreLoginCodeRepository(this.firestore, this.collectionLoginCodes);
	}

	@Bean
	public LoginStateRepository loginStateRepository() {
		return new FirestoreLoginStateRepository(this.firestore, this.collectionLoginStates);
	}
}
