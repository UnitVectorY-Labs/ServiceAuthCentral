/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.unitvectory.serviceauthcentral.server.token.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.unitvectory.serviceauthcentral.util.InputPatterns;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Token Request
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class TokenRequest {

	// Must be `client_credentials` or `urn:ietf:params:oauth:grant-type:jwt-bearer`
	@NotNull(message = "The request is missing the required parameter 'grant_type'.")
	private String grant_type;

	@NotNull(message = "The request is missing the required parameter 'client_id'.")
	@Pattern(regexp = InputPatterns.CLIENT_ID, message = "Invalid 'client_id' attribute format.")
	private String client_id;

	@Pattern(regexp = InputPatterns.CLIENT_SECRET,
			message = "Invalid 'client_secret' attribute format.")
	private String client_secret;

	@Pattern(regexp = InputPatterns.CLIENT_ID, message = "Invalid 'audience' attribute format.")
	private String audience;

	@Pattern(regexp = InputPatterns.SCOPES, message = "Invalid 'scope' attribute format.")
	private String scope;

	@Pattern(regexp = InputPatterns.JWT, message = "Invalid 'assertion' attribute format.")
	private String assertion;

	@Pattern(regexp = InputPatterns.AUTH_CODE, message = "Invalid 'code' attribute format.")
	private String code;

	private String redirect_uri;

	@Pattern(regexp = InputPatterns.PKCE_CODE_VERIFIER,
			message = "Invalid 'code_verifier' attribute format.")
	private String code_verifier;
}
