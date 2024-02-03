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
package com.unitvectory.auth.server.manage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Configuration
@EnableWebSecurity
public class MyCustomSecurityConfiguration {

	@Value("${sac.issuer}")
	private String issuer;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// JWT Decoding and validation
		NimbusJwtDecoder jwtDecoder =
				NimbusJwtDecoder.withJwkSetUri(this.issuer + "/.well-known/jwks.json").build();
		OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(this.issuer);
		OAuth2TokenValidator<Jwt> withAudience = new AudienceClaimValidator(this.issuer);
		OAuth2TokenValidator<Jwt> validator =
				new DelegatingOAuth2TokenValidator<>(withIssuer, withAudience);
		jwtDecoder.setJwtValidator(validator);

		http
				// Specify the authorization rules
				// Require authentication for /graphql
				.authorizeHttpRequests(
						authorize -> authorize.requestMatchers("/graphql").authenticated()
								// Allow all other requests
								.anyRequest().permitAll())
				// Configure OAuth2 Resource Server
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder)));
		return http.build();
	}
}
