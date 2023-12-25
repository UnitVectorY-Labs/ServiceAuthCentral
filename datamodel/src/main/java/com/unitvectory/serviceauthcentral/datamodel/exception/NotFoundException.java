package com.unitvectory.serviceauthcentral.datamodel.exception;

import lombok.experimental.StandardException;

/**
 * Returns HTTP 404 response
 */
@StandardException
public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = 8591429427827851447L;

}
