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
import com.unitvectory.auth.datamodel.memory.model.MemoryLoginState;
import com.unitvectory.auth.datamodel.model.LoginState;
import com.unitvectory.auth.datamodel.repository.LoginStateRepository;

/**
 * The Memory Login State Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public class MemoryLoginStateRepository implements LoginStateRepository {

	private final Map<String, MemoryLoginState> loginStates =
			new HashMap<String, MemoryLoginState>();

	public void reset() {
		this.loginStates.clear();
	}

	@Override
	public void saveState(String sessionId, String clientId, String redirectUri,
			String primaryState, String primaryCodeChallenge, String secondaryState, long ttl) {
		MemoryLoginState state =
				MemoryLoginState.builder().clientId(clientId).redirectUri(redirectUri)
						.primaryState(primaryState).primaryCodeChallenge(primaryCodeChallenge)
						.secondaryState(secondaryState).ttl(ttl).build();
		loginStates.put(sessionId, state);
	}

	@Override
	public LoginState getState(String sessionId) {
		return loginStates.get(sessionId);
	}

	@Override
	public void deleteState(String sessionId) {
		loginStates.remove(sessionId);
	}
}
