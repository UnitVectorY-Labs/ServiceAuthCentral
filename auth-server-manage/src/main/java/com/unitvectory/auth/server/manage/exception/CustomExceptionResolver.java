package com.unitvectory.auth.server.manage.exception;

import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

import com.unitvectory.auth.util.exception.BadRequestException;
import com.unitvectory.auth.util.exception.ConflictException;
import com.unitvectory.auth.util.exception.ForbiddenException;
import com.unitvectory.auth.util.exception.InternalServerErrorException;
import com.unitvectory.auth.util.exception.NotFoundException;
import com.unitvectory.auth.util.exception.UnauthorizedException;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;

@Component
public class CustomExceptionResolver extends DataFetcherExceptionResolverAdapter {

	@Override
	protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
		if (ex instanceof BadRequestException) {
			return GraphqlErrorBuilder.newError().errorType(ErrorType.BAD_REQUEST).message(ex.getMessage())
					.path(env.getExecutionStepInfo().getPath()).location(env.getField().getSourceLocation()).build();
		} else if (ex instanceof ConflictException) {
			// BAD_REQUEST is the wrong error type but other error types are missing
			return GraphqlErrorBuilder.newError().errorType(ErrorType.BAD_REQUEST).message(ex.getMessage())
					.path(env.getExecutionStepInfo().getPath()).location(env.getField().getSourceLocation()).build();
		} else if (ex instanceof ForbiddenException) {
			return GraphqlErrorBuilder.newError().errorType(ErrorType.FORBIDDEN).message(ex.getMessage())
					.path(env.getExecutionStepInfo().getPath()).location(env.getField().getSourceLocation()).build();
		} else if (ex instanceof InternalServerErrorException) {
			return GraphqlErrorBuilder.newError().errorType(ErrorType.INTERNAL_ERROR).message(ex.getMessage())
					.path(env.getExecutionStepInfo().getPath()).location(env.getField().getSourceLocation()).build();
		} else if (ex instanceof NotFoundException) {
			return GraphqlErrorBuilder.newError().errorType(ErrorType.NOT_FOUND).message(ex.getMessage())
					.path(env.getExecutionStepInfo().getPath()).location(env.getField().getSourceLocation()).build();
		} else if (ex instanceof UnauthorizedException) {
			return GraphqlErrorBuilder.newError().errorType(ErrorType.UNAUTHORIZED).message(ex.getMessage())
					.path(env.getExecutionStepInfo().getPath()).location(env.getField().getSourceLocation()).build();
		} else {
			return null;
		}
	}
}