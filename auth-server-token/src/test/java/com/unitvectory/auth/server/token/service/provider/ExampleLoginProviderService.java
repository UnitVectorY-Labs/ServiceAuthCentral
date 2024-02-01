package com.unitvectory.auth.server.token.service.provider;

import org.springframework.stereotype.Service;

import com.unitvectory.auth.server.token.model.UserContext;

@Service
public class ExampleLoginProviderService implements LoginProviderService {

	public static final String PROVIDER = "github";

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public String getClientId() {
		return PROVIDER + ":example";
	}

	@Override
	public UserContext authorizationCodeToUserContext(String code) {
		// TODO: return user context for valid test
		return null;
	}

	@Override
	public String getAuthorizationRedirectUri(String state) {
		return "http://localhost/auth";
	}

}
