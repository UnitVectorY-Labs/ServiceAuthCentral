package com.unitvectory.auth.datamodel.gcp.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.IgnoreExtraProperties;
import com.unitvectory.auth.datamodel.model.LoginState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IgnoreExtraProperties
public class LoginStateRecord implements LoginState {

	private String clientId;

	private String redirectUri;

	private String primaryState;

	private String primaryCodeChallenge;

	private String secondaryState;

	private Timestamp ttl;
}
