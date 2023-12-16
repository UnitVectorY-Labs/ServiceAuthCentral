package com.unitvectory.serviceauthcentral.service.time;

public class SystemTimeService implements TimeService {

	public long getCurrentTimeSeconds() {
		return System.currentTimeMillis() / 1000;
	}

}
