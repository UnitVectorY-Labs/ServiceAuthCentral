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

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Service interface to get the current time.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public interface TimeService {

	/**
	 * Gets the current time in seconds.
	 * 
	 * @return the current time in seconds
	 */
	long getCurrentTimeSeconds();

	/**
	 * Gets the current timestamp.
	 * 
	 * @return the timestamp
	 */
	default String getCurrentTimestamp() {
		return DateTimeFormatter.ISO_INSTANT
				.format(Instant.ofEpochSecond(this.getCurrentTimeSeconds()).atZone(ZoneOffset.UTC));
	}
}
