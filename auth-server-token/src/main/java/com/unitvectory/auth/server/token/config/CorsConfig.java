package com.unitvectory.auth.server.token.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

	@Value("${sac.cors.origins}")
	private List<String> corsOrigins;

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		// CORS configuration for /graphql endpoint
		CorsConfiguration tokenConfig = new CorsConfiguration();
		tokenConfig.setAllowedOrigins(this.corsOrigins);
		tokenConfig.setAllowedMethods(Collections.singletonList("POST"));
		tokenConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

		// Register configurations
		source.registerCorsConfiguration("/v1/token", tokenConfig);

		return new CorsFilter(source);
	}
}
