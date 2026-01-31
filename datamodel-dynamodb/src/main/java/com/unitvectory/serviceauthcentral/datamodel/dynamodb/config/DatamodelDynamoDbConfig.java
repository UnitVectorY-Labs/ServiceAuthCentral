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
package com.unitvectory.serviceauthcentral.datamodel.dynamodb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.unitvectory.consistgen.epoch.EpochTimeProvider;
import com.unitvectory.serviceauthcentral.datamodel.dynamodb.repository.DynamoDbAuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.dynamodb.repository.DynamoDbClientRepository;
import com.unitvectory.serviceauthcentral.datamodel.dynamodb.repository.DynamoDbJwkCacheRepository;
import com.unitvectory.serviceauthcentral.datamodel.dynamodb.repository.DynamoDbLoginCodeRepository;
import com.unitvectory.serviceauthcentral.datamodel.dynamodb.repository.DynamoDbLoginStateRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.JwkCacheRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.LoginCodeRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.LoginStateRepository;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

/**
 * The data model config for AWS DynamoDB
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Configuration
@Profile("datamodel-dynamodb")
public class DatamodelDynamoDbConfig {

	@Autowired
	private DynamoDbEnhancedClient dynamoDbEnhancedClient;

	@Autowired
	private EpochTimeProvider epochTimeProvider;

	@Value("${sac.datamodel.dynamodb.table.authorizations:sac-authorizations}")
	private String tableAuthorizations;

	@Value("${sac.datamodel.dynamodb.table.clients:sac-clients}")
	private String tableClients;

	@Value("${sac.datamodel.dynamodb.table.keys:sac-keys}")
	private String tableKeys;

	@Value("${sac.datamodel.dynamodb.table.logincodes:sac-loginCodes}")
	private String tableLoginCodes;

	@Value("${sac.datamodel.dynamodb.table.loginstates:sac-loginStates}")
	private String tableLoginStates;

	@Bean
	public AuthorizationRepository authorizationRepository() {
		return new DynamoDbAuthorizationRepository(this.dynamoDbEnhancedClient, this.tableAuthorizations,
				this.epochTimeProvider);
	}

	@Bean
	public ClientRepository clientRepository() {
		return new DynamoDbClientRepository(this.dynamoDbEnhancedClient, this.tableClients,
				this.epochTimeProvider);
	}

	@Bean
	public JwkCacheRepository jwkCacheRepository() {
		return new DynamoDbJwkCacheRepository(this.dynamoDbEnhancedClient, this.tableKeys);
	}

	@Bean
	public LoginCodeRepository loginCodeRepository() {
		return new DynamoDbLoginCodeRepository(this.dynamoDbEnhancedClient, this.tableLoginCodes);
	}

	@Bean
	public LoginStateRepository loginStateRepository() {
		return new DynamoDbLoginStateRepository(this.dynamoDbEnhancedClient, this.tableLoginStates);
	}
}
