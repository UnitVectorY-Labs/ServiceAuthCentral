package com.unitvectory.auth.server.manage.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClientJwtBearerType {

	private final String id;

	private final String jwksUrl;

	private final String iss;

	private final String sub;

	private final String aud;
}
