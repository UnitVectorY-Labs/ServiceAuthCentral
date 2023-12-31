package com.unitvectory.auth.sign.local.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.unitvectory.auth.sign.local.service.LocalSignService;
import com.unitvectory.auth.sign.service.SignService;

@Configuration
@Profile("sign-local")
public class SignLocalConfig {

	@Bean
	public SignService signService() {
		return new LocalSignService();
	}
}
