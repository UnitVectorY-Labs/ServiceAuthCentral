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
package com.unitvectory.auth.datamodel.gcp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.cloud.firestore.Firestore;
import com.unitvectory.auth.common.service.time.TimeService;
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

/**
 * The data model config for GCP
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Configuration
@Profile("datamodel-gcp")
public class DatamodelGcpConfig {

	@Autowired
	private Firestore firestore;

	@Autowired
	private TimeService timeService;

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
		return new FirestoreClientRepository(this.firestore, this.collectionClients,
				this.timeService);
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
