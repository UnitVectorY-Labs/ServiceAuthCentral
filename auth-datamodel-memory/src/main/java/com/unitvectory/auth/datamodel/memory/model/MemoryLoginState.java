package com.unitvectory.auth.datamodel.memory.model;

import com.unitvectory.auth.datamodel.model.LoginState;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class MemoryLoginState implements LoginState {

	private String clientId;

	private String redirectUri;

	private String primaryState;

	private String primaryCodeChallenge;

	private String secondaryState;

	private long ttl;
}
