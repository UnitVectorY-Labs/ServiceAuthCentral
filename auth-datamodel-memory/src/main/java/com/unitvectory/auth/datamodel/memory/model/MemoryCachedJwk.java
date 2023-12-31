package com.unitvectory.auth.datamodel.memory.model;

import com.unitvectory.auth.datamodel.model.CachedJwk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class MemoryCachedJwk implements CachedJwk {

	private String url;

	private long ttl;

	private boolean valid;

	private String kid;

	private String kty;

	private String alg;

	private String use;

	private String n;

	private String e;

	@Override
	public boolean isExpired(long now) {
		return ttl < now;
	}
}
