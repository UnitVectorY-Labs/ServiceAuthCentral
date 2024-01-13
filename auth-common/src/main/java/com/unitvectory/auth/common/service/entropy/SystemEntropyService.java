package com.unitvectory.auth.common.service.entropy;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Provides a source of random information.
 */
public class SystemEntropyService implements EntropyService {

	private static final String ALPHANUMERIC_CHARS =
			"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	private static final SecureRandom random = new SecureRandom();

	@Override
	public String generateUuid() {
		return UUID.randomUUID().toString();
	}

	@Override
	public String randomAlphaNumeric(int length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int randomIndex = random.nextInt(ALPHANUMERIC_CHARS.length());
			sb.append(ALPHANUMERIC_CHARS.charAt(randomIndex));
		}
		return sb.toString();
	}
}
