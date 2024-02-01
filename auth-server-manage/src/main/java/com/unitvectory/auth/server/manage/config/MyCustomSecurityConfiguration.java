package com.unitvectory.auth.server.manage.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MyCustomSecurityConfiguration {

	@Value("${sac.issuer}")
	private String issuer;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				// Specify the authorization rules
				// Require authentication for /graphql
				.authorizeHttpRequests(
						authorize -> authorize.requestMatchers("/graphql").authenticated()
								// Allow all other requests
								.anyRequest().permitAll())
				// Configure OAuth2 Resource Server
				.oauth2ResourceServer(oauth2 -> oauth2
						.jwt(jwt -> jwt.jwkSetUri(this.issuer + "/.well-known/jwks.json")));
		return http.build();
	}

	@Bean
	public NimbusJwtDecoder jwtDecoder() {
		NimbusJwtDecoder jwtDecoder =
				NimbusJwtDecoder.withJwkSetUri(this.issuer + "/.well-known/jwks.json").build();

		OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(this.issuer);
		OAuth2TokenValidator<Jwt> withAudience = new JwtClaimValidator<List<String>>(
				OAuth2TokenIntrospectionClaimNames.AUD, aud -> aud.contains(this.issuer));

		OAuth2TokenValidator<Jwt> validator =
				new DelegatingOAuth2TokenValidator<>(withIssuer, withAudience);

		jwtDecoder.setJwtValidator(validator);

		return jwtDecoder;
	}

}
