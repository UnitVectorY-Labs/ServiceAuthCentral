package com.unitvectory.serviceauthcentral.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;

public class BadRequestExceptionTest {

	@Test
	public void testBadRequestExceptionConstructor() {
		BindingResult mockBindingResult = mock(BindingResult.class);
		BadRequestException exception = new BadRequestException(mockBindingResult);

		assertNotNull(exception);
		assertEquals(mockBindingResult, exception.getResult());
	}
}