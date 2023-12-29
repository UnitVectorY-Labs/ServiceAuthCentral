package com.unitvectory.auth.datamodel.exception;

import lombok.experimental.StandardException;

/**
 * Returns HTTP 500 response
 */
@StandardException
public class InternalServerErrorException extends RuntimeException {

	private static final long serialVersionUID = 1432370721614074480L;

}