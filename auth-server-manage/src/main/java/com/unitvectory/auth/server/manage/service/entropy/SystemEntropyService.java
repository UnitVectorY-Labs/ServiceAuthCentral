package com.unitvectory.auth.server.manage.service.entropy;

import java.security.SecureRandom;

public class SystemEntropyService implements EntropyService {

	private static final String ALPHANUMERIC_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	private static final SecureRandom random = new SecureRandom();

	private static final int LENGTH = 32;

	@Override
	public String generateRandom() {
		StringBuilder sb = new StringBuilder(LENGTH);
		for (int i = 0; i < LENGTH; i++) {
			int randomIndex = random.nextInt(ALPHANUMERIC_CHARS.length());
			sb.append(ALPHANUMERIC_CHARS.charAt(randomIndex));
		}
		return sb.toString();
	}
}
