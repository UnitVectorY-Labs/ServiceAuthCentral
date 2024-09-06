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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * The security configuration for testing that disabled authentication.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@TestConfiguration
@EnableWebSecurity
@Profile("test")
public class TestSecurityConfig {

    @Value("${sac.issuer}")
    private String issuer;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Mock the JwtDecoder class
        JwtDecoder jwtDecoder = Mockito.mock(JwtDecoder.class);

        // Mock behavior to return different JWTs based on the token string
        when(jwtDecoder.decode(anyString())).thenAnswer(invocation -> {
            String token = invocation.getArgument(0);

            // Customize based on different token values that can be used in different tests
            if ("admin-token".equals(token)) {
                return createMockJwt("admin-user", "Admin");
            } else if ("user-token".equals(token)) {
                return createMockJwt("read-user", "Read");
            } else {
                return createMockJwt("guest-user", null);
            }
        });

        http.csrf(s -> s.disable()).authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                // Configure OAuth2 Resource Server
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder)));
        return http.build();
    }

    /**
     * Create a mock JWT for testing.
     * 
     * @param subject the subject
     * @param scope   the scope
     * @return the JWT
     */
    private Jwt createMockJwt(String subject, String scope) {
        Jwt.Builder builder = Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim(JwtClaimNames.SUB, subject)
                .claim(JwtClaimNames.AUD, this.issuer)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600));

        if (scope != null) {
            builder.claim("scope", scope);
        }

        return builder.build();
    }
}
