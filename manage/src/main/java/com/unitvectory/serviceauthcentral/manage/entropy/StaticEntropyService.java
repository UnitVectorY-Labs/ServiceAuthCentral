package com.unitvectory.serviceauthcentral.manage.entropy;

public class StaticEntropyService implements EntropyService {

	private final String val;

	public StaticEntropyService() {
		this("0000000000000000000000000000000000");
	}

	public StaticEntropyService(String val) {
		this.val = val;
	}

	@Override
	public String generateRandom() {
		return this.val;
	}
}