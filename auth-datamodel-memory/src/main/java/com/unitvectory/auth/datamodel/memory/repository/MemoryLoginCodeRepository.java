/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.unitvectory.auth.datamodel.memory.repository;

import java.util.HashMap;
import java.util.Map;
import com.unitvectory.auth.datamodel.memory.model.MemoryLoginCode;
import com.unitvectory.auth.datamodel.model.LoginCode;
import com.unitvectory.auth.datamodel.repository.LoginCodeRepository;

/**
 * The Memory Login Code Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
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
