package com.unitvectory.auth.datamodel.memory.repository;

import java.util.HashMap;
import java.util.Map;
import com.unitvectory.auth.datamodel.memory.model.MemoryLoginState;
import com.unitvectory.auth.datamodel.model.LoginState;
import com.unitvectory.auth.datamodel.repository.LoginStateRepository;

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
