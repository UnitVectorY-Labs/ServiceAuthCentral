package com.unitvectory.auth.common.service.time;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SystemTimeServiceTest {

	@Test
	public void getCurrentTimeSecondsTest() {
		TimeService service = new SystemTimeService();

		long now = service.getCurrentTimeSeconds();

		assertTrue(now > 0);
	}
}
