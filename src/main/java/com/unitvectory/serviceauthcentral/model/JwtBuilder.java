package com.unitvectory.serviceauthcentral.model;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.ObjectMapper;

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
			String encodedHeader = Base64.getUrlEncoder().withoutPadding()
					.encodeToString(MAPPER.writeValueAsString(headers).getBytes(StandardCharsets.UTF_8));
			String encodedPayload = Base64.getUrlEncoder().withoutPadding()
					.encodeToString(MAPPER.writeValueAsString(payload).getBytes(StandardCharsets.UTF_8));

			// Concatenate Encoded Header and Payload
			String unsignedToken = encodedHeader + "." + encodedPayload;

			return unsignedToken;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
