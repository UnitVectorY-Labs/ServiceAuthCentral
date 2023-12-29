package com.unitvectory.auth.server.token.service.entropy;

public class StaticEntropyService implements EntropyService {

	private final String uuid;

	public StaticEntropyService() {
		this("00000000-0000-0000-0000-000000000000");
	}

	public StaticEntropyService(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String generateUuid() {
		return this.uuid;
	}
}