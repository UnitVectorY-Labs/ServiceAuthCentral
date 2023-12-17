package com.unitvectory.serviceauthcentral.dto;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import com.auth0.jwk.Jwk;
import com.google.cloud.Timestamp;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class CachedJwk {

	private final long ONE_WEEK = 604800l;

	private final String url;

	private final String kid;

	private final Instant cached;

	private final Jwk jwk;

	public CachedJwk(String url, String kid, Instant cached) {
		this.url = url;
		this.kid = kid;
		this.cached = cached;
		this.jwk = null;
	}

	public CachedJwk(Map<String, Object> map) {
		Map<String, Object> copy = new HashMap<>(map);
		this.url = (String) copy.remove("url");
		this.cached = Instant.ofEpochSecond((Long) copy.remove("cached"));
		this.kid = (String) copy.get("kid");
		if (copy.containsKey("alg")) {
			this.jwk = Jwk.fromValues(copy);
		} else {
			this.jwk = null;
		}
	}

	public boolean isExpiredAfterSeconds(long now, long expiresSeconds) {
		if (this.cached == null) {
			return true;
		}

		return this.cached.getEpochSecond() < (now - expiresSeconds);
	}

	public boolean isExpiredAfterMinutes(long now, long expiresMinutes) {
		return this.isExpiredAfterSeconds(now, expiresMinutes * 60);
	}

	public boolean isExpiredAfterHours(long now, long expiresHours) {
		return this.isExpiredAfterSeconds(now, expiresHours * 60 * 60);
	}

	@Nonnull
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		if (jwk != null) {
			map.put("kid", jwk.getId());
			map.put("kty", jwk.getType());
			map.put("alg", jwk.getAlgorithm());
			map.put("use", jwk.getUsage());
			map.put("key_ops", jwk.getOperations());
			map.put("x5u", jwk.getCertificateUrl());
			map.put("x5c", jwk.getCertificateChain());
			map.put("x5t", jwk.getCertificateThumbprint());
			map.putAll(jwk.getAdditionalAttributes());
		} else {
			map.put("kid", this.kid);
		}

		// Save the time it was cached
		map.put("cached", this.cached.toEpochMilli() / 1000);

		long ttl = this.cached.getEpochSecond() - ONE_WEEK;
		Timestamp ts = Timestamp.ofTimeSecondsAndNanos(ttl, 0);
		map.put("ttl", ts);

		// url is not a real attribute but is very useful
		map.put("url", url);

		// Remove null values from the map
		map.values().removeIf(Objects::isNull);

		return map;
	}
}
