package com.unitvectory.serviceauthcentral.service.entropy;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.unitvectory.serviceauthcentral.service.entropy.EntropyService;
import com.unitvectory.serviceauthcentral.service.entropy.SystemEntropyService;

public class SystemEntropyServiceTest {

	@Test
	public void getCurrentTimeSecondsTest() {
		EntropyService service = new SystemEntropyService();
		String uuid = service.generateUuid();
		assertNotNull(uuid);
	}
}
