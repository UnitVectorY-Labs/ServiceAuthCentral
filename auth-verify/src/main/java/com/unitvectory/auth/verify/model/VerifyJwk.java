package com.unitvectory.auth.verify.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class VerifyJwk {

	private String kty;

	private String use;

	private String kid;

	private String alg;

	private String n;

	private String e;

}
