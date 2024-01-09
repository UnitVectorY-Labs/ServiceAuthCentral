package com.unitvectory.auth.server.manage.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthorizationType {

	private final String id;

	private final String subjectId;

	private final String audienceId;

}
