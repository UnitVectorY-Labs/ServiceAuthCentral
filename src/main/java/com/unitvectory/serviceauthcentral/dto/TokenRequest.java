package com.unitvectory.serviceauthcentral.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class TokenRequest {

	// Must be client_credentials
	private String grant_type;

	private String client_id;

	private String client_secret;

	private String audience;

	// Must be urn:ietf:params:oauth:client-assertion-type:jwt-bearer
	private String client_assertion_type;

	// Populated with JWT
	private String client_assertion;

}
