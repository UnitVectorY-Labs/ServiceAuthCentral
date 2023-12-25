package com.unitvectory.serviceauthcentral.datamodel.exception;

import org.springframework.validation.BindingResult;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.StandardException;

/**
 * Returns HTTP 400 response
 */
@StandardException
@AllArgsConstructor
public class BadRequestException extends RuntimeException {

	private static final long serialVersionUID = 2081978490501397300L;

	@Getter
	private BindingResult result;

}
