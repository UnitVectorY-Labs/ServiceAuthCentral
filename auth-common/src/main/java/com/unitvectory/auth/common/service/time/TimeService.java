package com.unitvectory.auth.common.service.time;

/**
 * Service interface to get the current time.
 */
public interface TimeService {

	/**
	 * Gets the current time in seconds.
	 * 
	 * @return the current time in seconds
	 */
	long getCurrentTimeSeconds();
}
