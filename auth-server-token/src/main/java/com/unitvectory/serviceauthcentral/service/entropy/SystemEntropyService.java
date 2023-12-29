package com.unitvectory.serviceauthcentral.service.entropy;

import java.util.UUID;

public class SystemEntropyService implements EntropyService {

	@Override
	public String generateUuid() {
		return UUID.randomUUID().toString();
	}

}
