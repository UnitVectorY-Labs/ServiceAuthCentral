package com.unitvectory.serviceauthcentral.service.time;

public class SystemTimeService implements TimeService {

	@Override
	public long getCurrentTimeSeconds() {
		return System.currentTimeMillis() / 1000;
	}

}
