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
package com.unitvectory.serviceauthcentral.server.manage.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * The CORS Configuration
 * 
 * This allows POST to /graphql to the configured origins
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Configuration
public class CorsConfig {

	@Value("${sac.cors.origins}")
	private List<String> corsOrigins;

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		// CORS configuration for /graphql endpoint
		CorsConfiguration graphqlConfig = new CorsConfiguration();
		graphqlConfig.setAllowedOrigins(this.corsOrigins);
		graphqlConfig.setAllowedMethods(Arrays.asList("POST", "OPTIONS"));
		graphqlConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

		// Register configurations
		source.registerCorsConfiguration("/graphql", graphqlConfig);

		return new CorsFilter(source);
	}
}
