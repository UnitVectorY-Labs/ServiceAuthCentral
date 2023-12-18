package com.unitvectory.serviceauthcentral.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.auth0.jwk.Jwk;

class CachedJwkTest {

	@Test
	void missingCached() {
		String url = "http://example.com";
		String kid = "keyId";

		CachedJwk jwk = new CachedJwk(url, kid, null);

		long now = Instant.now().getEpochSecond();
		long expires = 1000l;

		assertTrue(jwk.isExpiredAfterHours(now, expires));
		assertTrue(jwk.isExpiredAfterMinutes(now, expires));
		assertTrue(jwk.isExpiredAfterSeconds(now, expires));

	}

	@Test
	void construct_withMapParameters() {
		Map<String, Object> map = this.createMockJwkMap();
		CachedJwk jwk = new CachedJwk(map);

		assertEquals(map.get("url"), jwk.getUrl());
		assertEquals(map.get("kid"), jwk.getKid());

	}

	@Test
	void constructor_withValidParameters_shouldSetFieldsCorrectly() {
		String url = "http://example.com";
		String kid = "keyId";
		Instant cached = Instant.now();

		CachedJwk jwk = new CachedJwk(url, kid, cached);

		assertEquals(url, jwk.getUrl());
		assertEquals(kid, jwk.getKid());
		assertEquals(cached, jwk.getCached());
		assertNull(jwk.getJwk());
	}

	@Test
	void constructor_withValidMap_shouldSetFieldsCorrectly() {
		Map<String, Object> map = new HashMap<>();
		map.put("url", "http://example.com");
		map.put("kid", "keyId");
		map.put("cached", Instant.now().getEpochSecond());

		CachedJwk jwk = new CachedJwk(map);

		assertEquals("http://example.com", jwk.getUrl());
		assertEquals("keyId", jwk.getKid());
		assertNotNull(jwk.getCached());
		assertNull(jwk.getJwk());
	}

	@Test
	void isExpiredAfterSeconds_withNotExpiredTime_shouldReturnFalse() {
		CachedJwk jwk = new CachedJwk("http://example.com", "keyId", Instant.now());
		assertFalse(jwk.isExpiredAfterSeconds(Instant.now().getEpochSecond(), 3600));
	}

	@Test
	void isExpiredAfterSeconds_withExpiredTime_shouldReturnTrue() {
		CachedJwk jwk = new CachedJwk("http://example.com", "keyId", Instant.now().minusSeconds(7200));
		assertTrue(jwk.isExpiredAfterSeconds(Instant.now().getEpochSecond(), 3600));
	}

	@Test
	void toMap_withJwkObject_shouldContainCorrectValues() {
		// Assuming a method to create or mock a Jwk object
		Jwk mockJwk = createMockJwk(); // Replace with actual mocking code
		CachedJwk jwk = new CachedJwk("http://example.com", "keyId", Instant.now(), mockJwk);
		Map<String, Object> map = jwk.toMap();

		// Assertions to check map content
		assertNotNull(map.get("kid"));
		assertNotNull(map.get("cached"));
		// Add more assertions based on mockJwk's expected behavior
	}

	@Test
	void toMap_withoutJwkObject_shouldContainCorrectValues() {
		CachedJwk jwk = new CachedJwk("http://example.com", "keyId", Instant.now());
		Map<String, Object> map = jwk.toMap();

		assertEquals("keyId", map.get("kid"));
		assertNotNull(map.get("cached"));
		assertNull(map.get("alg")); // Assuming 'alg' is null when Jwk is null
	}

	private Map<String, Object> createMockJwkMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("alg", "RS256");
		map.put("cached", 1702695055l);
		map.put("e", "AQAB");
		map.put("kid", "9b0285c31bfd8b040e03157b19c4e960bdc10c6f");
		map.put("kty", "RSA");
		map.put("n",
				"uCYe4j3rIDaC9U8jCloiD5UP5cQCndcKr570LSxEznqNB0qpmtqDJBU-RuSJbMEYZ853AlezSWca8uqDBAgdIWPod-scaQTOTg049m9hFwQuP7FzXsAjtxiOHub0nrD60Dy7vI1dPoiyiFdox25JUdW6OSPyq2OlFxCPIQy4SpKvebXduA2ZeIY5TWE2wt0mVPo__s9NACn4Ni9GwsPCcgG6yn8oAJ-JW6xCLnz5_CycNlg178Sxj8LWVEisPbdEK9LhSwQ7V3YU7pfLpEAtGWHYrIcH3-Tfz6IkS9-UmAzbdjaGk2W-AXkZW8jiIbfNER7e4ZKLntC4Am4InHkJzw");
		map.put("url", "https://www.googleapis.com/oauth2/v3/certs");
		map.put("use", "sig");
		return map;
	}

	private Jwk createMockJwk() {
		return Jwk.fromValues(createMockJwkMap());
	}
}
