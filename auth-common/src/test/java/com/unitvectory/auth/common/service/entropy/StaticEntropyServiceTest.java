package com.unitvectory.auth.common.service.entropy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.unitvectory.auth.common.entropy.EntropyService;
import com.unitvectory.auth.common.entropy.StaticEntropyService;

public class StaticEntropyServiceTest {

	@Test
	public void nullStaticEntropyServiceTest() {

		NullPointerException thrown = assertThrows(NullPointerException.class, () -> {
			new StaticEntropyService(null, 'A');
		});

		assertEquals("uuid is marked non-null but is null", thrown.getMessage());
	}

	@Test
	public void generateUuidDefaultTest() {
		EntropyService entropy = new StaticEntropyService();

		assertEquals("00000000-0000-0000-0000-000000000000", entropy.generateUuid());
	}

	@Test
	public void randomAlphaNumericDefaultTest() {
		EntropyService entropy = new StaticEntropyService();

		assertEquals("", entropy.randomAlphaNumeric(0));
		assertEquals("A", entropy.randomAlphaNumeric(1));
		assertEquals("AAAAAAAAAA", entropy.randomAlphaNumeric(10));
	}

	@Test
	public void generateUuidTest() {
		EntropyService entropy = new StaticEntropyService("11111111-0000-0000-0000-000000000000", 'B');

		assertEquals("11111111-0000-0000-0000-000000000000", entropy.generateUuid());
	}

	@Test
	public void randomAlphaNumericTest() {
		EntropyService entropy = new StaticEntropyService("11111111-0000-0000-0000-000000000000", 'B');

		assertEquals("", entropy.randomAlphaNumeric(0));
		assertEquals("B", entropy.randomAlphaNumeric(1));
		assertEquals("BBBBBBBBBB", entropy.randomAlphaNumeric(10));
	}
}
