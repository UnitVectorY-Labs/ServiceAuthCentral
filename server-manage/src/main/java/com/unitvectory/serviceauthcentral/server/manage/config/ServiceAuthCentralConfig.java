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
package com.unitvectory.serviceauthcentral.server.manage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.unitvectory.consistgen.epoch.EpochTimeProvider;
import com.unitvectory.consistgen.epoch.SystemEpochTimeProvider;
import com.unitvectory.consistgen.string.RandomStringProvider;
import com.unitvectory.consistgen.string.StringProvider;
import com.unitvectory.consistgen.uuid.RandomUuidGenerator;
import com.unitvectory.consistgen.uuid.UuidGenerator;
import com.unitvectory.serviceauthcentral.server.manage.service.AuthorizationService;
import com.unitvectory.serviceauthcentral.server.manage.service.ClientService;
import com.unitvectory.serviceauthcentral.server.manage.service.DefaultAuthorizationService;
import com.unitvectory.serviceauthcentral.server.manage.service.DefaultClientService;
import com.unitvectory.serviceauthcentral.server.manage.service.DefaultManagementCapabilitiesService;
import com.unitvectory.serviceauthcentral.server.manage.service.ManagementCapabilitiesService;

/**
 * The Service Auth Central configuration
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Configuration
public class ServiceAuthCentralConfig {

	@Bean
	public EpochTimeProvider epochTimeProvider() {
		return SystemEpochTimeProvider.getInstance();
	}

	@Bean
	public UuidGenerator uuidGenerator() {
		return RandomUuidGenerator.getInstance();
	}

	@Bean
	public StringProvider stringProvider() {
		return RandomStringProvider.getInstance();
	}

	@Bean
	public ClientService clientService() {
		return new DefaultClientService();
	}

	@Bean
	public AuthorizationService authorizationService() {
		return new DefaultAuthorizationService();
	}

	@Bean
	ManagementCapabilitiesService managementCapabilitiesService() {
		return new DefaultManagementCapabilitiesService();
	}
}
