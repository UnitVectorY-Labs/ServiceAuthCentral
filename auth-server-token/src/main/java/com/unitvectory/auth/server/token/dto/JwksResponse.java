package com.unitvectory.auth.server.token.dto;

import java.util.List;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
public class JwksResponse {

	@Singular
	private List<JwksKey> keys;
}
