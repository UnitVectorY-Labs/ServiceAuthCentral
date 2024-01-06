package com.unitvectory.auth.common.service.time;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.unitvectory.auth.common.time.StaticTimeService;
import com.unitvectory.auth.common.time.TimeService;

public class StaticTimeServiceTest {

	@Test
	public void defaultTimeTest() {
		TimeService time = new StaticTimeService();
		assertEquals(1701388800l, time.getCurrentTimeSeconds());
	}

	@Test
	public void timeTest() {
		TimeService time = new StaticTimeService(1701300000l);
		assertEquals(1701300000l, time.getCurrentTimeSeconds());
	}
}
