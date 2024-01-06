package com.unitvectory.auth.common.time;

/**
 * Provides the current time.
 */
public class SystemTimeService implements TimeService {

	@Override
	public long getCurrentTimeSeconds() {
		return System.currentTimeMillis() / 1000;
	}

}
