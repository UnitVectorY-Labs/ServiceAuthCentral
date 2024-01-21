package com.unitvectory.auth.datamodel.memory.model;

import com.unitvectory.auth.datamodel.model.LoginCode;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class MemoryLoginCode implements LoginCode {

	private String clientId;

	private String redirectUri;

	private String codeChallenge;

	private String userClientId;

	private long ttl;

}
