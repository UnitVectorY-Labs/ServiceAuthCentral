package com.unitvectory.auth.server.token.service.entropy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.unitvectory.auth.server.token.service.entropy.EntropyService;
import com.unitvectory.auth.server.token.service.entropy.StaticEntropyService;

public class StaticEntropyServiceTest {

	@Test
	public void defaultTest() {
		EntropyService entropy = new StaticEntropyService();
		assertEquals("00000000-0000-0000-0000-000000000000", entropy.generateUuid());
	}

	public void genTest() {
		EntropyService entropy = new StaticEntropyService("11111111-0000-0000-0000-000000000000");
		assertEquals("11111111-0000-0000-0000-000000000000", entropy.generateUuid());
	}
}
