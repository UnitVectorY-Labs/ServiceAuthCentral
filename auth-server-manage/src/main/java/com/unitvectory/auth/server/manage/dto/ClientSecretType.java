package com.unitvectory.auth.server.manage.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClientSecretType {

	private final String clientSecret;
}
