package com.unitvectory.auth.common.entropy;

import lombok.NonNull;

/**
 * Provides a mechanism to have the methods that normally return random
 * information return static information which is useful for testing.
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