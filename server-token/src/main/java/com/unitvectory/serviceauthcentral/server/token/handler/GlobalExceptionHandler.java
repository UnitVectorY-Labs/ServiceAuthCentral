/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.unitvectory.serviceauthcentral.server.token.handler;

import java.util.concurrent.ExecutionException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.unitvectory.serviceauthcentral.server.token.dto.ErrorResponse;
import com.unitvectory.serviceauthcentral.util.exception.BadRequestException;
import com.unitvectory.serviceauthcentral.util.exception.ForbiddenException;
import com.unitvectory.serviceauthcentral.util.exception.InternalServerErrorException;
import com.unitvectory.serviceauthcentral.util.exception.NotFoundException;
import com.unitvectory.serviceauthcentral.util.exception.UnauthorizedException;

/**
 * The global exception handler
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex,
			WebRequest request) {
		ErrorResponse.ErrorResponseBuilder builder = ErrorResponse.builder()
				.error("invalid_request").status(400).message(ex.getMessage());
		/*
		 * if (ex.getResult() != null) { // Multiple errors, include all of them for (ObjectError
		 * error : ex.getResult().getAllErrors()) { builder.message(error.getDefaultMessage()); } }
		 * else { builder.message(ex.getMessage()); }
		 */

		return new ResponseEntity<ErrorResponse>(builder.build(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex,
			WebRequest request) {
		ErrorResponse error = ErrorResponse.builder().error("invalid_client").status(404)
				.message(ex.getMessage()).build();
		return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex,
			WebRequest request) {
		ErrorResponse error = ErrorResponse.builder().error("invalid_client").status(403)
				.message(ex.getMessage()).build();
		return new ResponseEntity<ErrorResponse>(error, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(InternalServerErrorException.class)
	public ResponseEntity<ErrorResponse> handleInternalServerErrorException(
			InternalServerErrorException ex, WebRequest request) {
		ErrorResponse error = ErrorResponse.builder().error("server_error").status(500)
				.message(ex.getMessage()).build();
		return new ResponseEntity<ErrorResponse>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex,
			WebRequest request) {
		ErrorResponse error = ErrorResponse.builder().error("access_denied").status(401)
				.message(ex.getMessage()).build();
		return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex,
			WebRequest request) {
		ErrorResponse error = ErrorResponse.builder().error("server_error").status(500)
				.message(ex.getMessage()).build();
		return new ResponseEntity<ErrorResponse>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(InterruptedException.class)
	public ResponseEntity<ErrorResponse> handleInterruptedException(InterruptedException ex,
			WebRequest request) {
		ErrorResponse error = ErrorResponse.builder().error("server_error").status(500)
				.message("process interrupted").build();
		return new ResponseEntity<ErrorResponse>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ExecutionException.class)
	public ResponseEntity<ErrorResponse> handleExecutionException(ExecutionException ex,
			WebRequest request) {
		ErrorResponse error = ErrorResponse.builder().error("server_error").status(500)
				.message("execution exception").build();
		return new ResponseEntity<ErrorResponse>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(java.lang.NullPointerException.class)
	public ResponseEntity<ErrorResponse> handleNullPointerException(
			java.lang.NullPointerException ex, WebRequest request) {
		ErrorResponse error = ErrorResponse.builder().error("server_error").status(500)
				.message(ex.getMessage()).build();
		return new ResponseEntity<ErrorResponse>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
