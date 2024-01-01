package com.unitvectory.auth.server.token.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(Include.NON_NULL)
public class JwksKey {

	private String kty;

	private String kid;

	private String use;

	private String alg;

	private String n;

	private String e;

}
