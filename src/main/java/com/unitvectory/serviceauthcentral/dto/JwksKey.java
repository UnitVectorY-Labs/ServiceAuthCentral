package com.unitvectory.serviceauthcentral.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Data;

@Data
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
