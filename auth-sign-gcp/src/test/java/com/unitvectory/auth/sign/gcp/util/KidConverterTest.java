package com.unitvectory.auth.sign.gcp.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class KidConverterTest {

	@Test
	public void hashTest() {
		assertEquals("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08", KidConverter.hash("test"));
	}
}