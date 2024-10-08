/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.unitvectory.serviceauthcentral.datamodel.firestore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.cloud.firestore.Firestore;
import com.unitvectory.consistgen.epoch.EpochTimeProvider;
import com.unitvectory.serviceauthcentral.datamodel.firestore.repository.FirestoreAuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.firestore.repository.FirestoreClientRepository;
import com.unitvectory.serviceauthcentral.datamodel.firestore.repository.FirestoreJwkCacheRepository;
import com.unitvectory.serviceauthcentral.datamodel.firestore.repository.FirestoreLoginCodeRepository;
import com.unitvectory.serviceauthcentral.datamodel.firestore.repository.FirestoreLoginStateRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.JwkCacheRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.LoginCodeRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.LoginStateRepository;

/**
 * The data model config for GCP
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Configuration
@Profile("datamodel-firestore")
public class DatamodelGcpConfig {

	@Autowired
	private Firestore firestore;

	@Autowired
	private EpochTimeProvider epochTimeProvider;

	@Value("${sac.datamodel.firestore.collection.authorizations:authorizations}")
	private String collectionAuthorizations;

	@Value("${sac.datamodel.firestore.collection.clients:clients}")
	private String collectionClients;

	@Value("${sac.datamodel.firestore.collection.keys:keys}")
	private String collectionKeys;

	@Value("${sac.datamodel.firestore.collection.logincodes:loginCodes}")
	private String collectionLoginCodes;

	@Value("${sac.datamodel.firestore.collection.loginstates:loginStates}")
	private String collectionLoginStates;

	@Bean
	public AuthorizationRepository authorizationRepository() {
		return new FirestoreAuthorizationRepository(this.firestore, this.collectionAuthorizations,
				this.epochTimeProvider);
	}

	@Bean
	public ClientRepository clientRepository() {
		return new FirestoreClientRepository(this.firestore, this.collectionClients,
				this.epochTimeProvider);
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
