package com.unitvectory.auth.verify.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class VerifyParameters {

	private final String iss;

	private final String sub;

	private final String aud;

}
