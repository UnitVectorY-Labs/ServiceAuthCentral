package com.unitvectory.auth.sign.local.model;

import com.unitvectory.auth.sign.model.SignJwk;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(setterPrefix = "with")
public class JsonWebKeyRecord implements SignJwk {

	private final String kty;

	private final String kid;

	private final String use;

	private final String alg;

	private final String n;

	private final String e;

}
