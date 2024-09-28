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
package com.unitvectory.serviceauthcentral.server.token.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import com.unitvectory.consistgen.epoch.EpochTimeProvider;
import com.unitvectory.consistgen.epoch.StaticEpochTimeProvider;
import com.unitvectory.consistgen.string.StaticStringProvider;
import com.unitvectory.consistgen.string.StringProvider;
import com.unitvectory.consistgen.uuid.StaticUuidGenerator;
import com.unitvectory.consistgen.uuid.UuidGenerator;
import com.unitvectory.serviceauthcentral.datamodel.memory.repository.MemoryAuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.memory.repository.MemoryClientRepository;
import com.unitvectory.serviceauthcentral.datamodel.memory.repository.MemoryJwkCacheRepository;
import com.unitvectory.serviceauthcentral.datamodel.memory.repository.MemoryLoginCodeRepository;
import com.unitvectory.serviceauthcentral.datamodel.memory.repository.MemoryLoginStateRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.JwkCacheRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.LoginCodeRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.LoginStateRepository;
import com.unitvectory.serviceauthcentral.server.token.service.LocalJwksResolver;
import com.unitvectory.serviceauthcentral.verify.auth0.service.Auth0JwtVerifier;
import com.unitvectory.serviceauthcentral.verify.service.JwksResolver;
import com.unitvectory.serviceauthcentral.verify.service.JwtVerifier;

/**
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@TestConfiguration
@Profile("test")
public class TestServiceAuthCentralConfig {

	@Bean
	public EpochTimeProvider epochTimeProvider() {
		return StaticEpochTimeProvider.builder().epochTimeSeconds(1701388800l).build();
	}

	@Bean
	public UuidGenerator uuidGenerator() {
		return StaticUuidGenerator.builder().uuid("00000000-0000-0000-0000-000000000000").build();
	}

	@Bean
	public StringProvider stringProvider() {
		return StaticStringProvider.getInstance();
	}

	@Bean
	public AuthorizationRepository authorizationRepository(EpochTimeProvider epochTimeProvider) {
		return new MemoryAuthorizationRepository(epochTimeProvider);
	}

	@Bean
	public ClientRepository clientRepository(EpochTimeProvider epochTimeProvider) {
		return new MemoryClientRepository(epochTimeProvider);
	}

	@Bean
	public JwkCacheRepository jwkCacheRepository() {
		return new MemoryJwkCacheRepository();
	}

	@Bean
	public LoginCodeRepository loginCodeRepository() {
		return new MemoryLoginCodeRepository();
	}

	@Bean
	public LoginStateRepository loginStateRepository() {
		return new MemoryLoginStateRepository();
	}

	@Bean
	public JwksResolver jwksResolver() {
		return new LocalJwksResolver();
	}

	@Bean
	public JwtVerifier jwtVerifier() {
		// This implementation works for testing
		return new Auth0JwtVerifier();
	}
}
