package com.unitvectory.serviceauthcentral.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(Include.NON_NULL)
public class ErrorResponse {

	private String error;

	private String message;

	private String details;
}
