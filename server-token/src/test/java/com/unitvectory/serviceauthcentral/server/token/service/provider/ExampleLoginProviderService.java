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
package com.unitvectory.serviceauthcentral.server.token.service.provider;

import org.springframework.stereotype.Service;

import com.unitvectory.serviceauthcentral.server.token.model.UserContext;

/**
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
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

	@Override
	public String getProviderDisplayName() {
		return "Example";
	}

}
