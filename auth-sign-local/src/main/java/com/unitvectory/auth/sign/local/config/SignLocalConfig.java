package com.unitvectory.auth.sign.local.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.unitvectory.auth.sign.local.service.LocalSignService;
import com.unitvectory.auth.sign.service.SignService;

@Configuration
@Profile("sign-local")
public class SignLocalConfig {

	@Value("${serviceauthcentral.sign.local.active.kid}")
	private String activeKid;

	@Value("${serviceauthcentral.sign.local.key1.privatekey}")
	private String key1PrivateKey;

	@Value("${serviceauthcentral.sign.local.key1.publickey}")
	private String key1PublicKey;

	@Value("${serviceauthcentral.sign.local.key1.kid}")
	private String key1Kid;

	@Bean
	public SignService signService() {
		return new LocalSignService(this.activeKid, this.key1PrivateKey, this.key1PublicKey, this.key1Kid);
	}
}
