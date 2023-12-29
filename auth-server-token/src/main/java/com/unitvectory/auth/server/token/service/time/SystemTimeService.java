package com.unitvectory.auth.server.token.service.time;

public class SystemTimeService implements TimeService {

	@Override
	public long getCurrentTimeSeconds() {
		return System.currentTimeMillis() / 1000;
	}

}
