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
package com.unitvectory.auth.sign.local.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.unitvectory.auth.sign.local.service.LocalSignService;
import com.unitvectory.auth.sign.service.SignService;

/**
 * The config for using local JWT signing.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Configuration
@Profile("sign-local")
public class SignLocalConfig {

	@Value("${sac.sign.local.active.kid}")
	private String activeKid;

	@Value("${sac.sign.local.key1.privatekey}")
	private String key1PrivateKey;

	@Value("${sac.sign.local.key1.publickey}")
	private String key1PublicKey;

	@Value("${sac.sign.local.key1.kid}")
	private String key1Kid;

	@Bean
	public SignService signService() {
		return new LocalSignService(this.activeKid, this.key1PrivateKey, this.key1PublicKey,
				this.key1Kid);
	}
}
