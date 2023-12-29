package com.unitvectory.auth.server.token.service.entropy;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.unitvectory.auth.server.token.service.entropy.EntropyService;
import com.unitvectory.auth.server.token.service.entropy.SystemEntropyService;

public class SystemEntropyServiceTest {

	@Test
	public void getCurrentTimeSecondsTest() {
		EntropyService service = new SystemEntropyService();
		String uuid = service.generateUuid();
		assertNotNull(uuid);
	}
}
