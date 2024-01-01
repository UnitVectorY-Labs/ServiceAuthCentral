package com.unitvectory.auth.sign.gcp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.unitvectory.auth.sign.gcp.service.KmsSignService;
import com.unitvectory.auth.sign.service.SignService;

@Configuration
@Profile("sign-gcp")
public class SignGcpConfig {

	@Autowired
	private KeyManagementServiceClient keyManagementServiceClient;

	@Autowired
	private String keyManagementServiceKeyName;

	@Value("${serviceauthcentral.sign.gcp.cache.jwks.seconds:3600}")
	private long cacheJwksSeconds;

	@Value("${serviceauthcentral.sign.gcp.cache.safety.multiple:24}")
	private int cacheSafetyMultiple;

	@Bean
	public SignService signService() {
		return new KmsSignService(this.keyManagementServiceClient, this.keyManagementServiceKeyName,
				this.cacheJwksSeconds, this.cacheSafetyMultiple);
	}
}
