package com.unitvectory.serviceauthcentral.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(setterPrefix = "with")
@JsonInclude(Include.NON_NULL)
public class TokenResponse {

	private String access_token;

	private String token_type;

	private long expires_in;
}
