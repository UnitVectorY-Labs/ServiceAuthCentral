package com.unitvectory.auth.server.token.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.unitvectory.auth.server.token.service.entropy.EntropyService;
import com.unitvectory.auth.server.token.service.entropy.SystemEntropyService;
import com.unitvectory.auth.server.token.service.jwk.CachedJwkService;
import com.unitvectory.auth.server.token.service.jwk.JwksService;
import com.unitvectory.auth.server.token.service.jwk.RemoteJwksService;
import com.unitvectory.auth.server.token.service.time.SystemTimeService;
import com.unitvectory.auth.server.token.service.time.TimeService;

@Configuration
@Profile("!test")
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
	public JwksService jwksService() {
		return new CachedJwkService(new RemoteJwksService());
	}
}
