package com.unitvectory.auth.sign.gcp.model;

import com.unitvectory.auth.sign.model.JsonWebKey;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(setterPrefix = "with")
public class JsonWebKeyRecord implements JsonWebKey {

	private final String kty;

	private final String kid;

	private final String use;

	private final String alg;

	private final String n;

	private final String e;

	// Internal attributes needed for book keeping

	private final String keyName;

	private final boolean active;

	private final long created;

}
