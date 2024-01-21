package com.unitvectory.auth.server.manage.dto;

import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClientType {

	private final String clientId;

	private final String description;

	private final String clientType;

	private final boolean clientSecret1Set;

	private final boolean clientSecret2Set;

	private final List<ClientJwtBearerType> jwtBearer;
}
