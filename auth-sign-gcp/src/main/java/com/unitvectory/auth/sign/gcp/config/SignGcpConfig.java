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
package com.unitvectory.auth.sign.gcp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.unitvectory.auth.sign.gcp.service.KmsSignService;
import com.unitvectory.auth.sign.service.SignService;

/**
 * The GCP Sign Config
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Configuration
@Profile("sign-gcp")
public class SignGcpConfig {

	@Autowired
	private KeyManagementServiceClient keyManagementServiceClient;

	@Autowired
	private String keyManagementServiceKeyName;

	@Value("${sac.sign.gcp.cache.jwks.seconds:3600}")
	private long cacheJwksSeconds;

	@Value("${sac.sign.gcp.cache.safety.multiple:24}")
	private int cacheSafetyMultiple;

	@Bean
	public SignService signService() {
		return new KmsSignService(this.keyManagementServiceClient, this.keyManagementServiceKeyName,
				this.cacheJwksSeconds, this.cacheSafetyMultiple);
	}
}
