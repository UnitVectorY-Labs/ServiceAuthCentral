package com.unitvectory.auth.verify.model;

import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class VerifyJwks {

	private List<VerifyJwk> keys;

}
