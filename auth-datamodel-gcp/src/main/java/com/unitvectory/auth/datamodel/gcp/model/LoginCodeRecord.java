package com.unitvectory.auth.datamodel.gcp.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.IgnoreExtraProperties;
import com.unitvectory.auth.datamodel.model.LoginCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IgnoreExtraProperties
public class LoginCodeRecord implements LoginCode {

	private String clientId;

	private String redirectUri;

	private String codeChallenge;

	private String userClientId;

	private Timestamp ttl;

}
