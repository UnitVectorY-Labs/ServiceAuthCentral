package com.unitvectory.auth.datamodel.memory.repository;

import java.util.HashMap;
import java.util.Map;
import com.unitvectory.auth.datamodel.memory.model.MemoryLoginCode;
import com.unitvectory.auth.datamodel.model.LoginCode;
import com.unitvectory.auth.datamodel.repository.LoginCodeRepository;

public class MemoryLoginCodeRepository implements LoginCodeRepository {

	private final Map<String, MemoryLoginCode> loginCodes = new HashMap<String, MemoryLoginCode>();

	public void reset() {
		this.loginCodes.clear();
	}

	@Override
	public void saveCode(String code, String clientId, String redirectUri, String codeChallenge,
			String userClientId, long ttl) {
		MemoryLoginCode loginCode =
				MemoryLoginCode.builder().clientId(clientId).redirectUri(redirectUri)
						.codeChallenge(codeChallenge).userClientId(userClientId).ttl(ttl).build();
		this.loginCodes.put(code, loginCode);
	}

	@Override
	public LoginCode getCode(String code) {
		return this.loginCodes.get(code);
	}

	@Override
	public void deleteCode(String code) {
		this.loginCodes.remove(code);
	}
}
