package com.unitvectory.serviceauthcentral.service;

public class SystemTimeService implements TimeService {

	public long getCurrentTimeSeconds() {
		return System.currentTimeMillis() / 1000;
	}

}
