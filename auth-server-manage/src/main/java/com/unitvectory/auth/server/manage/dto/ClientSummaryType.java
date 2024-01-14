package com.unitvectory.auth.server.manage.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClientSummaryType {

	private final String clientId;

	private final String description;
}
