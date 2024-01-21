package com.unitvectory.auth.server.manage.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		// CORS configuration for /graphql endpoint
		CorsConfiguration graphqlConfig = new CorsConfiguration();
		graphqlConfig.setAllowedOrigins(Collections.singletonList("*"));
		graphqlConfig.setAllowedMethods(Collections.singletonList("POST"));
		graphqlConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

		// Register configurations
		source.registerCorsConfiguration("/graphql", graphqlConfig);

		return new CorsFilter(source);
	}
}
