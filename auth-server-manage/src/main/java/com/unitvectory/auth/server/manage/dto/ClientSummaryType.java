package com.unitvectory.auth.server.manage.dto;

import com.unitvectory.auth.datamodel.model.ClientSummary;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClientSummaryType implements ClientSummary {

	private final String clientId;

	private final String description;
}
