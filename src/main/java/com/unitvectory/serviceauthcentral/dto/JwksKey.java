package com.unitvectory.serviceauthcentral.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(setterPrefix = "with")
@JsonInclude(Include.NON_NULL)
public class JwksKey {

	private String kty;

	private String kid;

	private String use;

	private String alg;

	private String n;

	private String e;

	/*
	 * Attributes not returned, but needed to determine which key to use when
	 * signing the JWT
	 */

	@JsonIgnore
	private String keyName;

	@JsonIgnore
	private boolean active;

	@JsonIgnore
	private long created;

}
