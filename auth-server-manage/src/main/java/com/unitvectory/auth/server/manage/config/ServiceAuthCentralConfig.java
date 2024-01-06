package com.unitvectory.auth.server.manage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.unitvectory.auth.common.entropy.EntropyService;
import com.unitvectory.auth.common.entropy.SystemEntropyService;
import com.unitvectory.auth.server.manage.service.AuthorizationService;
import com.unitvectory.auth.server.manage.service.ClientService;
import com.unitvectory.auth.server.manage.service.DefaultAuthorizationService;
import com.unitvectory.auth.server.manage.service.DefaultClientService;

@Configuration
public class ServiceAuthCentralConfig {

	@Bean
	public EntropyService entropyService() {
		return new SystemEntropyService();
	}

	@Bean
	public ClientService clientService() {
		return new DefaultClientService();
	}

	@Bean
	AuthorizationService authorizationService() {
		return new DefaultAuthorizationService();
	}
}
