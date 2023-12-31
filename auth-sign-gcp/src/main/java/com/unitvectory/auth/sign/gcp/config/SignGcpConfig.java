package com.unitvectory.auth.sign.gcp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.unitvectory.auth.sign.gcp.service.KmsSignService;
import com.unitvectory.auth.sign.service.SignService;

@Configuration
@Profile("sign-gcp")
public class SignGcpConfig {

	@Bean
	public SignService signService() {
		return new KmsSignService();
	}
}
