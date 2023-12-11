package com.unitvectory.serviceauthcentral.exception;

import lombok.experimental.StandardException;

/**
 * Returns HTTP 403 response
 */
@StandardException
public class ForbiddenException extends RuntimeException {

	private static final long serialVersionUID = -5129321449851403766L;

}
