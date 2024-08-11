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
package com.unitvectory.serviceauthcentral.common.service.time;

/**
 * Provides a mechanism to have the method that normally return the current time return static time
 * which is useful for testing.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public class StaticTimeService implements TimeService {

	private final long now;

	public StaticTimeService() {
		// Friday, December 1, 2023 12:00:00 AM
		this(1701388800l);
	}

	public StaticTimeService(long now) {
		this.now = now;
	}

	@Override
	public long getCurrentTimeSeconds() {
		return this.now;
	}
}
