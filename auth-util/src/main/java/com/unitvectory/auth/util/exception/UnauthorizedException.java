package com.unitvectory.auth.util.exception;

import lombok.experimental.StandardException;

/**
 * Returns HTTP 401 response
 */
@StandardException
public class UnauthorizedException extends RuntimeException {

	private static final long serialVersionUID = -6707964794235626508L;

}
