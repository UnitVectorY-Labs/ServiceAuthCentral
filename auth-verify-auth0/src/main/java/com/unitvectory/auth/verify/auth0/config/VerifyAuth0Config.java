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
package com.unitvectory.auth.verify.auth0.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.unitvectory.auth.verify.auth0.service.Auth0JwksResolver;
import com.unitvectory.auth.verify.auth0.service.Auth0JwtVerifier;
import com.unitvectory.auth.verify.service.JwksResolver;
import com.unitvectory.auth.verify.service.JwtVerifier;

/**
 * The Verify configuration for using Auth0's library implementation.
 * 
 * This does not depend on the Auth0 service, just their library implementation.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Configuration
@Profile("verify-auth0")
public class VerifyAuth0Config {

	@Bean
	public JwksResolver jwksResolver() {
		return new Auth0JwksResolver();
	}

	@Bean
	public JwtVerifier jwtVerifier() {
		return new Auth0JwtVerifier();
	}
}
