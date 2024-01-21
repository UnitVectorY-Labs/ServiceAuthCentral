package com.unitvectory.auth.datamodel.couchbase.model;

import com.unitvectory.auth.datamodel.model.LoginCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginCodeRecord implements LoginCode {

	private String clientId;

	private String redirectUri;

	private String codeChallenge;

	private String userClientId;

	private long ttl;

}
