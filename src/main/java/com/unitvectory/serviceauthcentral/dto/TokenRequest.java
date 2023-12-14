package com.unitvectory.serviceauthcentral.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

	// Must be `client_credentials` or `urn:ietf:params:oauth:grant-type:jwt-bearer`
	@NotNull(message = "The request is missing the required parameter 'grant_type'.pp")
	private String grant_type;

	@NotNull(message = "The request is missing the required parameter 'client_id'.pp")
	@Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Invalid 'client_id' attribute format.")
	private String client_id;

	@Pattern(regexp = "^[0-9a-zA-Z]{16,64}$", message = "Invalid 'client_secret' attribute format.")
	private String client_secret;

	@NotNull(message = "The request is missing the required parameter 'audience'.pp")
	@Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Invalid 'audience' attribute format.")
	private String audience;

	@NotNull(message = "The request is missing the required parameter 'assertion'.pp")
	@Pattern(regexp = "^[\\w-]+\\.[\\w-]+\\.[\\w-]+$", message = "Invalid 'assertion' attribute format.")
	private String assertion;

}
