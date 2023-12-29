package com.unitvectory.auth.server.token.service.time;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.unitvectory.auth.server.token.service.time.SystemTimeService;
import com.unitvectory.auth.server.token.service.time.TimeService;

public class SystemTimeServiceTest {

	@Test
	public void getCurrentTimeSecondsTest() {
		TimeService service = new SystemTimeService();
		long now = service.getCurrentTimeSeconds();

		assertTrue(now > 0);
	}
}
