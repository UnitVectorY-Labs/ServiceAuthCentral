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

import com.unitvectory.serviceauthcentral.common.service.entropy.EntropyService;
import com.unitvectory.serviceauthcentral.common.service.entropy.SystemEntropyService;
import com.unitvectory.serviceauthcentral.common.service.time.SystemTimeService;
import com.unitvectory.serviceauthcentral.common.service.time.TimeService;
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
	TimeService timeService() {
		return new SystemTimeService();
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
	public AuthorizationService authorizationService() {
		return new DefaultAuthorizationService();
	}

	@Bean
	ManagementCapabilitiesService managementCapabilitiesService() {
		return new DefaultManagementCapabilitiesService();
	}
}
