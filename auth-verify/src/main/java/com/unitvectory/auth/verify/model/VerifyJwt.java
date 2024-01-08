package com.unitvectory.auth.verify.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class VerifyJwt {

	private final String kid;

	private final String iss;

	private final String sub;

	private final String aud;
}
