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
package com.unitvectory.auth.common.service.time;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
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
