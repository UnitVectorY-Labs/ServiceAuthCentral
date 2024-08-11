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

import lombok.NonNull;

/**
 * Provides a mechanism to have the methods that normally return random information return static
 * information which is useful for testing.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public class StaticEntropyService implements EntropyService {

	private final String uuid;

	private final char ch;

	public StaticEntropyService() {
		this("00000000-0000-0000-0000-000000000000", 'A');
	}

	public StaticEntropyService(@NonNull String uuid, char ch) {
		this.uuid = uuid;
		this.ch = ch;
	}

	@Override
	public String generateUuid() {
		return this.uuid;
	}

	@Override
	public String randomAlphaNumeric(int length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(ch);
		}
		return sb.toString();
	}
}
