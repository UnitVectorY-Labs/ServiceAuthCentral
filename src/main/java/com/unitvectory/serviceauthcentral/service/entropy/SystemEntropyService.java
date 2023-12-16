package com.unitvectory.serviceauthcentral.service.entropy;

import java.util.UUID;

public class SystemEntropyService implements EntropyService {

	public String generateUuid() {
		return UUID.randomUUID().toString();
	}

}
