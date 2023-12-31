package com.unitvectory.auth.server.manage.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClientType {

	private final String clientId;

	private final boolean clientSecret1Set;

	private final boolean clientSecret2Set;

}
