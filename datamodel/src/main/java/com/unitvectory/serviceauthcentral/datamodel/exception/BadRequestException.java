package com.unitvectory.serviceauthcentral.datamodel.exception;

import lombok.experimental.StandardException;

/**
 * Returns HTTP 400 response
 */
@StandardException
public class BadRequestException extends RuntimeException {

	private static final long serialVersionUID = 2081978490501397300L;

}
