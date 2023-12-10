package com.unitvectory.serviceauthcentral.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.unitvectory.serviceauthcentral.service.CloudKeyService;
import com.unitvectory.serviceauthcentral.service.EntropyService;
import com.unitvectory.serviceauthcentral.service.KeyService;
import com.unitvectory.serviceauthcentral.service.SystemEntropyService;
import com.unitvectory.serviceauthcentral.service.SystemTimeService;
import com.unitvectory.serviceauthcentral.service.TimeService;

@Configuration
public class ServiceAuthCentralConfig {

	@Bean
	public TimeService timeService() {
		return new SystemTimeService();
	}

	@Bean
	public EntropyService entropyService() {
		return new SystemEntropyService();
	}

	@Bean
	public KeyService keyService() {
		return new CloudKeyService();
	}
}
