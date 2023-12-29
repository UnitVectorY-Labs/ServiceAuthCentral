package com.unitvectory.auth.server.token.config;

import org.springframework.context.annotation.Bean;

public class AppPropertyConfig {

	@Bean
	public AppConfig appConfig() {
		return new AppConfig();
	}
}
