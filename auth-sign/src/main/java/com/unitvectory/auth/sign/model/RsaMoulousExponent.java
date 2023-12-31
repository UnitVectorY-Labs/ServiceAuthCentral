package com.unitvectory.auth.sign.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RsaMoulousExponent {

	private final String modulus;

	private final String exponent;

}
