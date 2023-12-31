package com.unitvectory.auth.datamodel.gcp.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.IgnoreExtraProperties;
import com.unitvectory.auth.datamodel.model.CachedJwk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IgnoreExtraProperties
public class CachedJwkRecord implements CachedJwk {

	@DocumentId
	private String documentId;

	private String url;

	private Timestamp ttl;

	private boolean valid;

	private String kid;

	private String kty;

	private String alg;

	private String use;

	private String n;

	private String e;

	@Override
	public boolean isExpired(long now) {
		return ttl.getSeconds() < now;
	}
}
