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
package com.unitvectory.serviceauthcentral.server.token.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.unitvectory.consistgen.epoch.EpochTimeProvider;
import com.unitvectory.consistgen.string.StringProvider;
import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientScope;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientType;
import com.unitvectory.serviceauthcentral.datamodel.model.LoginState;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.LoginCodeRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.LoginStateRepository;
import com.unitvectory.serviceauthcentral.user.LoginUserService;
import com.unitvectory.serviceauthcentral.user.UserContext;
import com.unitvectory.serviceauthcentral.util.exception.BadRequestException;
import com.unitvectory.serviceauthcentral.util.exception.InternalServerErrorException;
import com.unitvectory.serviceauthcentral.util.exception.UnauthorizedException;

import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;

/**
 * The Login Service for authenticating user accounts with double oauth.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Service
public class LoginService {

	private static final long AUTH_CODE_VALID_SECONDS = 60;

	@Value("${sac.user.redirecturi}")
	private List<String> primaryRedirectUris;

	@Autowired
	private EpochTimeProvider epochTimeProvider;

	@Autowired
	private StringProvider stringProvider;

	@Autowired
	private LoginCodeRepository loginCodeRepository;

	@Autowired
	private LoginStateRepository loginStateRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private List<LoginUserService> loginUserService;

	private LoginUserService getLoginProvider(@NonNull String clientId) {
		for (LoginUserService loginProvider : this.loginUserService) {
			if (!loginProvider.isActive()) {
				continue;
			}

			if (clientId.equals(loginProvider.getClientId())) {
				return loginProvider;
			}
		}

		return null;
	}

	public String authorize(@NonNull String sessionId, @NonNull String responseType,
			@NonNull String clientId, @NonNull String redirectUri, @NonNull String codeChallenge,
			@NonNull String codeChallengeMethod, @NonNull String state) {

		// First phase is input validation

		if (!"code".equals(responseType)) {
			throw new RuntimeException(
					"Provided 'response_type' is invalid. Only supports 'code'.");
		}

		LoginUserService loginUserService = this.getLoginProvider(clientId);
		if (loginUserService == null) {
			throw new BadRequestException("Provided 'client_id' is invalid");
		}

		if (primaryRedirectUris == null) {
			throw new InternalServerErrorException("redirect uris not configured");
		} else if (!primaryRedirectUris.contains(redirectUri)) {
			// If the redirect URI isn't one of the configured valid values
			throw new BadRequestException("Provided 'redirect_uri' is invalid");
		}

		if (!"S256".equalsIgnoreCase(codeChallengeMethod)) {
			throw new BadRequestException(
					"Provided 'code_challenge_method' is invalid. Only supports 'S256'.");
		}

		// Next phase is generating the dynamic information that is needed

		String secondaryState = this.stringProvider.generate(25);

		long ttl = this.epochTimeProvider.epochTimeSeconds() + AUTH_CODE_VALID_SECONDS;

		// Now we can save the state into the database

		this.loginStateRepository.saveState(sessionId, clientId, redirectUri, state, codeChallenge,
				secondaryState, ttl);

		// And we are done, generate the redirect URI

		return loginUserService.getAuthorizationRedirectUri(secondaryState);
	}

	public String callback(@NonNull String sessionId, @NonNull String code, @NonNull String state,
			HttpServletResponse response) {

		LoginState loginState = this.loginStateRepository.getState(sessionId);
		if (loginState == null) {
			throw new UnauthorizedException("Unauthorized sessionId");
		}

		String clientId = loginState.getClientId();
		String redirectUri = loginState.getRedirectUri();
		String primaryState = loginState.getPrimaryState();
		String primaryCodeChallenge = loginState.getPrimaryCodeChallenge();
		String secondaryState = loginState.getSecondaryState();

		LoginUserService loginUserService = this.getLoginProvider(clientId);
		if (loginUserService == null) {
			throw new BadRequestException("Provided 'client_id' is invalid");
		}

		if (secondaryState == null || state == null || !secondaryState.equals(state)) {
			throw new RuntimeException("Invalid state parameter");
		}

		// Perform the actual exchange of the auth code and retrieve the user
		// information from the third party

		UserContext userContext = loginUserService.authorizationCodeToUserContext(code);
		if (userContext == null) {
			throw new InternalServerErrorException("Unable to exchange authorization code");
		}

		if (userContext.getUserId() == null) {
			throw new RuntimeException("failed to get userId");
		}

		// Look up to see if the client exists

		String userClientId = userContext.getUserClientId();
		Client userClient = this.clientRepository.getClient(userClientId);
		if (userClient == null) {
			// Create the client does not exist, create it

			String salt = this.stringProvider.generate(32);
			this.clientRepository.putClient(userClientId,
					loginUserService.getServiceDisplayName() + " User: " + userContext.getUserName(), salt,
					ClientType.USER,
					new ArrayList<ClientScope>());
		}

		// Now we can generate the data needed to redirect the user

		String authCode = this.stringProvider.generate(25);
		long ttl = this.epochTimeProvider.epochTimeSeconds() + 60;

		// Save the authorization code to the database

		this.loginCodeRepository.saveCode(authCode, clientId, redirectUri, primaryCodeChallenge,
				userClientId, ttl);

		// Now that we have the authorization code generated we can just go ahead and
		// delete the state record

		this.loginStateRepository.deleteState(sessionId);

		// Finally, construct the redirect

		return String.format("%s?code=%s&state=%s", redirectUri, authCode, primaryState);
	}
}
