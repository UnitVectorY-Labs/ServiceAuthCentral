package com.unitvectory.auth.datamodel.couchbase.model;

import com.unitvectory.auth.datamodel.model.LoginState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginStateRecord implements LoginState {

	private String clientId;

	private String redirectUri;

	private String primaryState;

	private String primaryCodeChallenge;

	private String secondaryState;

	private long ttl;
}
