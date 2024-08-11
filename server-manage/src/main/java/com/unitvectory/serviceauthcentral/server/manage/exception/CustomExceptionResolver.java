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
package com.unitvectory.serviceauthcentral.server.manage.exception;

import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.unitvectory.serviceauthcentral.util.exception.BadRequestException;
import com.unitvectory.serviceauthcentral.util.exception.ConflictException;
import com.unitvectory.serviceauthcentral.util.exception.ForbiddenException;
import com.unitvectory.serviceauthcentral.util.exception.InternalServerErrorException;
import com.unitvectory.serviceauthcentral.util.exception.NotFoundException;
import com.unitvectory.serviceauthcentral.util.exception.UnauthorizedException;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;

/**
 * The Custom Exception Resolver
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Component
public class CustomExceptionResolver extends DataFetcherExceptionResolverAdapter {

	@Override
	protected GraphQLError resolveToSingleError(@NonNull Throwable ex,
			@NonNull DataFetchingEnvironment env) {
		if (ex instanceof BadRequestException) {
			return GraphqlErrorBuilder.newError().errorType(ErrorType.BAD_REQUEST)
					.message(ex.getMessage()).path(env.getExecutionStepInfo().getPath())
					.location(env.getField().getSourceLocation()).build();
		} else if (ex instanceof ConflictException) {
			// BAD_REQUEST is the wrong error type but other error types are missing
			return GraphqlErrorBuilder.newError().errorType(ErrorType.BAD_REQUEST)
					.message(ex.getMessage()).path(env.getExecutionStepInfo().getPath())
					.location(env.getField().getSourceLocation()).build();
		} else if (ex instanceof ForbiddenException) {
			return GraphqlErrorBuilder.newError().errorType(ErrorType.FORBIDDEN)
					.message(ex.getMessage()).path(env.getExecutionStepInfo().getPath())
					.location(env.getField().getSourceLocation()).build();
		} else if (ex instanceof InternalServerErrorException) {
			return GraphqlErrorBuilder.newError().errorType(ErrorType.INTERNAL_ERROR)
					.message(ex.getMessage()).path(env.getExecutionStepInfo().getPath())
					.location(env.getField().getSourceLocation()).build();
		} else if (ex instanceof NotFoundException) {
			return GraphqlErrorBuilder.newError().errorType(ErrorType.NOT_FOUND)
					.message(ex.getMessage()).path(env.getExecutionStepInfo().getPath())
					.location(env.getField().getSourceLocation()).build();
		} else if (ex instanceof UnauthorizedException) {
			return GraphqlErrorBuilder.newError().errorType(ErrorType.UNAUTHORIZED)
					.message(ex.getMessage()).path(env.getExecutionStepInfo().getPath())
					.location(env.getField().getSourceLocation()).build();
		} else {
			return null;
		}
	}
}
