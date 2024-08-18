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
package com.unitvectory.serviceauthcentral.server.token.model;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The JWT Builder
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public class JwtBuilder {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private Map<String, Object> headers;

	private Map<String, Object> payload;

	private JwtBuilder() {
		this.headers = new TreeMap<String, Object>();
		this.payload = new TreeMap<String, Object>();

		// Common headers
		headers.put("alg", "RS256");
		headers.put("typ", "JWT");
	}

	public static JwtBuilder builder() {
		return new JwtBuilder();
	}

	public JwtBuilder withKeyId(String kid) {
		this.headers.put("kid", kid);
		return this;
	}

	public JwtBuilder withIssuer(String iss) {
		this.payload.put("iss", iss);
		return this;
	}

	public JwtBuilder withSubject(String sub) {
		this.payload.put("sub", sub);
		return this;
	}

	public JwtBuilder withAudience(String aud) {
		this.payload.put("aud", aud);
		return this;
	}

	public JwtBuilder withScopes(Set<String> scopes) {
		this.payload.put("scope", String.join(" ", scopes));
		return this;
	}

	public JwtBuilder withDescription(String description) {
		if (description != null) {
			this.payload.put("description", description);
		}

		return this;
	}

	public JwtBuilder withJwtId(String jti) {
		this.payload.put("jti", jti);
		return this;
	}

	public JwtBuilder withTiming(long now, long validSeconds) {
		payload.put("iat", now);
		payload.put("nbf", now);
		long exp = now + validSeconds;
		payload.put("exp", exp);
		return this;
	}

	public String buildUnsignedToken() {
		try {
			// Encode Header and Payload into JSON
			String encodedHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(
					MAPPER.writeValueAsString(headers).getBytes(StandardCharsets.UTF_8));
			String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(
					MAPPER.writeValueAsString(payload).getBytes(StandardCharsets.UTF_8));

			// Concatenate Encoded Header and Payload
			String unsignedToken = encodedHeader + "." + encodedPayload;

			return unsignedToken;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
