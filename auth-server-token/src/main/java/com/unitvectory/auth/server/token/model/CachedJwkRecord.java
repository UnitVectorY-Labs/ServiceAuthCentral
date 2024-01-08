package com.unitvectory.auth.server.token.model;

import com.unitvectory.auth.datamodel.model.CachedJwk;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CachedJwkRecord implements CachedJwk {

	private String url;

	private final boolean valid = true;

	private String kid;

	private String kty;

	private String alg;

	private String use;

	private String n;

	private String e;

	@Override
	public boolean isExpired(long now) {
		// Records in this context never expire
		return false;
	}
}
