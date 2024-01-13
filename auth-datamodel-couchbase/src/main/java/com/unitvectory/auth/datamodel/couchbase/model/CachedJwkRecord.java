package com.unitvectory.auth.datamodel.couchbase.model;

import com.unitvectory.auth.datamodel.model.CachedJwk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CachedJwkRecord implements CachedJwk {

	private String documentId;

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
