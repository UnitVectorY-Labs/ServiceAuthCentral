package com.unitvectory.serviceauthcentral.service.time;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.unitvectory.serviceauthcentral.service.time.SystemTimeService;
import com.unitvectory.serviceauthcentral.service.time.TimeService;

public class SystemTimeServiceTest {

	@Test
	public void getCurrentTimeSecondsTest() {
		TimeService service = new SystemTimeService();
		long now = service.getCurrentTimeSeconds();

		assertTrue(now > 0);
	}
}
