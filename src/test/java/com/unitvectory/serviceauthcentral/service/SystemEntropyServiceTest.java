package com.unitvectory.serviceauthcentral.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class SystemEntropyServiceTest {

	@Test
	public void getCurrentTimeSecondsTest() {
		EntropyService service = new SystemEntropyService();
		String uuid = service.generateUuid();
		assertNotNull(uuid);
	}
}
