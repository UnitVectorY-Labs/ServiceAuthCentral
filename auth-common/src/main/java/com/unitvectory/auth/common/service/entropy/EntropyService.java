package com.unitvectory.auth.common.service.entropy;

/**
 * Service interface for generating random information.
 */
public interface EntropyService {

	/**
	 * Generates a random UUID v4 string.
	 * 
	 * @return the uuid
	 */
	String generateUuid();

	/**
	 * Generates a random alpha numeric string of the specified length
	 * 
	 * @param length length of string
	 * @return random string
	 */
	String randomAlphaNumeric(int length);
}
