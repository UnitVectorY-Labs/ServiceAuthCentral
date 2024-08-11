/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.unitvectory.serviceauthcentral.common.service.entropy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

/**
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public class SystemEntropyServiceTest {

	private static final String UUID_PATTERN =
			"^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";

	private static final String ALPHA_PATTERN = "^[a-zA-Z0-9]+";

	@Test
	public void generateUuidTest() {
		EntropyService service = new SystemEntropyService();
		String uuid = service.generateUuid();

		assertNotNull(uuid);

		assertTrue(Pattern.matches(UUID_PATTERN, uuid),
				"The string should be a valid UUID version 4");
	}

	@Test
	public void randomAlphaNumericTest() {
		EntropyService service = new SystemEntropyService();

		String random0 = service.randomAlphaNumeric(0);
		assertNotNull(random0);
		assertEquals(0, random0.length());

		String random1 = service.randomAlphaNumeric(1);
		assertNotNull(random1);
		assertEquals(1, random1.length());
		assertTrue(Pattern.matches(ALPHA_PATTERN, random1), "");

		String random10 = service.randomAlphaNumeric(10);
		assertNotNull(random10);
		assertEquals(10, random10.length());
		assertTrue(Pattern.matches(ALPHA_PATTERN, random10), "");

	}
}
