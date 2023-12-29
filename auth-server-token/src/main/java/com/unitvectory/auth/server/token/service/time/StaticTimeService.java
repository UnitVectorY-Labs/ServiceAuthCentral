package com.unitvectory.auth.server.token.service.time;

public class StaticTimeService implements TimeService {

	private final long now;

	public StaticTimeService() {
		// Friday, December 1, 2023 12:00:00 AM
		this(1701388800l);
	}

	public StaticTimeService(long now) {
		this.now = now;
	}

	@Override
	public long getCurrentTimeSeconds() {
		return this.now;
	}
}
