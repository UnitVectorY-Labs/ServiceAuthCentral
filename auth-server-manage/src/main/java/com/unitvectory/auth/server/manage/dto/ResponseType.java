package com.unitvectory.auth.server.manage.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ResponseType {

	private final boolean success;
}
