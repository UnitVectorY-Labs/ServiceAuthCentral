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
package com.unitvectory.auth.common.service.entropy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
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
		EntropyService entropy =
				new StaticEntropyService("11111111-0000-0000-0000-000000000000", 'B');

		assertEquals("11111111-0000-0000-0000-000000000000", entropy.generateUuid());
	}

	@Test
	public void randomAlphaNumericTest() {
		EntropyService entropy =
				new StaticEntropyService("11111111-0000-0000-0000-000000000000", 'B');

		assertEquals("", entropy.randomAlphaNumeric(0));
		assertEquals("B", entropy.randomAlphaNumeric(1));
		assertEquals("BBBBBBBBBB", entropy.randomAlphaNumeric(10));
	}
}
