package com.unitvectory.serviceauthcentral.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
@JsonInclude(Include.NON_NULL)
public class ErrorResponse {

	private String error;

	@Singular
	private List<String> messages;

	private String details;

	private int status;
}
