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
package com.unitvectory.serviceauthcentral.server.manage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import com.unitvectory.serviceauthcentral.server.manage.dto.AuthorizationType;
import com.unitvectory.serviceauthcentral.server.manage.dto.ClientManagementCapabilitiesType;
import com.unitvectory.serviceauthcentral.server.manage.dto.ClientScopeType;
import com.unitvectory.serviceauthcentral.server.manage.dto.ClientSecretType;
import com.unitvectory.serviceauthcentral.server.manage.dto.ClientType;
import com.unitvectory.serviceauthcentral.server.manage.dto.RequestJwt;
import com.unitvectory.serviceauthcentral.server.manage.dto.ResponseType;
import com.unitvectory.serviceauthcentral.server.manage.mapper.RequestJwtMapper;
import com.unitvectory.serviceauthcentral.server.manage.service.ClientService;
import com.unitvectory.serviceauthcentral.server.manage.service.ManagementCapabilitiesService;
import com.unitvectory.serviceauthcentral.util.InputPatterns;
import com.unitvectory.serviceauthcentral.util.exception.BadRequestException;

/**
 * The GraphQL Client Resolver
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Controller
public class ClientResolver {

	@Autowired
	private ClientService clientService;

	@Autowired
	private ManagementCapabilitiesService managementCapabilitiesService;

	@MutationMapping
	public ClientType addClient(@Argument String clientId, @Argument String description,
			@Argument List<ClientScopeType> availableScopes, @AuthenticationPrincipal Jwt jwt) {

		// Authorize the request

		RequestJwt requestJwt = RequestJwtMapper.INSTANCE.requestJwt(jwt);
		if (!requestJwt.isWriteAuthorized()) {
			throw new BadRequestException("Unauthorized, missing required scope.");
		}

		// Input Validation

		if (clientId == null || !clientId.matches(InputPatterns.CLIENT_ID)) {
			throw new BadRequestException("Invalid 'client_id' attribute format.");
		} else if (description != null && !description.matches(InputPatterns.DESCRIPTION)) {
			throw new BadRequestException("Invalid 'description' attribute format.");
		}

		if (availableScopes != null) {
			for (ClientScopeType scope : availableScopes) {
				if (scope.getScope() == null || !scope.getScope().matches(InputPatterns.SCOPE)) {
					throw new BadRequestException("Invalid 'scope' attribute format.");
				} else if (scope.getDescription() == null
						|| !scope.getDescription().matches(InputPatterns.DESCRIPTION)) {
					throw new BadRequestException("Invalid 'description' attribute format.");
				}
			}
		}

		return this.clientService.addClient(clientId, description, availableScopes);
	}

	@MutationMapping
	public ResponseType addClientAvailableScope(@Argument String clientId,
			@Argument ClientScopeType availableScope, @AuthenticationPrincipal Jwt jwt) {

		// Authorize the request

		RequestJwt requestJwt = RequestJwtMapper.INSTANCE.requestJwt(jwt);
		if (!requestJwt.isWriteAuthorized()) {
			throw new BadRequestException("Unauthorized, missing required scope.");
		}

		// Input Validation

		if (clientId == null || !clientId.matches(InputPatterns.CLIENT_ID)) {
			throw new BadRequestException("Invalid 'client_id' attribute format.");
		} else if (availableScope == null) {
			throw new BadRequestException("Invalid 'availableScope' attribute format.");
		} else if (availableScope.getScope() == null
				|| !availableScope.getScope().matches(InputPatterns.SCOPE)) {
			throw new BadRequestException("Invalid 'scope' attribute format.");
		} else if (availableScope.getDescription() == null
				|| !availableScope.getDescription().matches(InputPatterns.DESCRIPTION)) {
			throw new BadRequestException("Invalid 'description' attribute format.");
		}

		return this.clientService.addClientAvailableScope(clientId, availableScope, requestJwt);
	}

	@MutationMapping
	public ResponseType deleteClient(@Argument String clientId, @AuthenticationPrincipal Jwt jwt) {

		// Authorize the request

		RequestJwt requestJwt = RequestJwtMapper.INSTANCE.requestJwt(jwt);
		if (!requestJwt.isWriteAuthorized()) {
			throw new BadRequestException("Unauthorized, missing required scope.");
		}

		// Input Validation

		if (clientId == null || !clientId.matches(InputPatterns.CLIENT_ID)) {
			throw new BadRequestException("Invalid 'client_id' attribute format.");
		}

		return this.clientService.deleteClient(clientId, requestJwt);
	}

	@MutationMapping
	public ClientSecretType generateClientSecret1(@Argument String clientId,
			@AuthenticationPrincipal Jwt jwt) {

		// Authorize the request

		RequestJwt requestJwt = RequestJwtMapper.INSTANCE.requestJwt(jwt);
		if (!requestJwt.isWriteAuthorized()) {
			throw new BadRequestException("Unauthorized, missing required scope.");
		}

		// Input Validation

		if (clientId == null || !clientId.matches(InputPatterns.CLIENT_ID)) {
			throw new BadRequestException("Invalid 'client_id' attribute format.");
		}

		return this.clientService.generateClientSecret1(clientId, requestJwt);
	}

	@MutationMapping
	public ClientSecretType generateClientSecret2(@Argument String clientId,
			@AuthenticationPrincipal Jwt jwt) {

		// Authorize the request

		RequestJwt requestJwt = RequestJwtMapper.INSTANCE.requestJwt(jwt);
		if (!requestJwt.isWriteAuthorized()) {
			throw new BadRequestException("Unauthorized, missing required scope.");
		}

		// Input Validation

		if (clientId == null || !clientId.matches(InputPatterns.CLIENT_ID)) {
			throw new BadRequestException("Invalid 'client_id' attribute format.");
		}

		return this.clientService.generateClientSecret2(clientId, requestJwt);
	}

	@MutationMapping
	public ClientSecretType clearClientSecret1(@Argument String clientId,
			@AuthenticationPrincipal Jwt jwt) {

		// Authorize the request

		RequestJwt requestJwt = RequestJwtMapper.INSTANCE.requestJwt(jwt);
		if (!requestJwt.isWriteAuthorized()) {
			throw new BadRequestException("Unauthorized, missing required scope.");
		}

		// Input Validation

		if (clientId == null || !clientId.matches(InputPatterns.CLIENT_ID)) {
			throw new BadRequestException("Invalid 'client_id' attribute format.");
		}

		return this.clientService.clearClientSecret1(clientId, requestJwt);
	}

	@MutationMapping
	public ClientSecretType clearClientSecret2(@Argument String clientId,
			@AuthenticationPrincipal Jwt jwt) {

		// Authorize the request

		RequestJwt requestJwt = RequestJwtMapper.INSTANCE.requestJwt(jwt);
		if (!requestJwt.isWriteAuthorized()) {
			throw new BadRequestException("Unauthorized, missing required scope.");
		}

		// Input Validation

		if (clientId == null || !clientId.matches(InputPatterns.CLIENT_ID)) {
			throw new BadRequestException("Invalid 'client_id' attribute format.");
		}

		return this.clientService.clearClientSecret2(clientId, requestJwt);
	}

	@MutationMapping
	public ResponseType authorizeJwtBearer(@Argument String clientId, @Argument String jwksUrl,
			@Argument String iss, @Argument String sub, @Argument String aud,
			@AuthenticationPrincipal Jwt jwt) {

		// Authorize the request

		RequestJwt requestJwt = RequestJwtMapper.INSTANCE.requestJwt(jwt);
		if (!requestJwt.isWriteAuthorized()) {
			throw new BadRequestException("Unauthorized, missing required scope.");
		}

		// Input Validation

		if (clientId == null || !clientId.matches(InputPatterns.CLIENT_ID)) {
			throw new BadRequestException("Invalid 'client_id' attribute format.");
		} else if (jwksUrl == null || !jwksUrl.matches(InputPatterns.EXTERNAL_JWKS_URL)) {
			throw new BadRequestException("Invalid 'jwks_url' attribute format.");
		} else if (iss == null || !iss.matches(InputPatterns.EXTERNAL_CLAIM)) {
			throw new BadRequestException("Invalid 'iss' attribute format.");
		} else if (sub == null || !sub.matches(InputPatterns.EXTERNAL_CLAIM)) {
			throw new BadRequestException("Invalid 'sub' attribute format.");
		} else if (aud == null || !aud.matches(InputPatterns.EXTERNAL_CLAIM)) {
			throw new BadRequestException("Invalid 'aud' attribute format.");
		}

		return this.clientService.authorizeJwtBearer(clientId, jwksUrl, iss, sub, aud, requestJwt);
	}

	@MutationMapping
	public ResponseType deauthorizeJwtBearer(@Argument String clientId, @Argument String id,
			@AuthenticationPrincipal Jwt jwt) {

		// Authorize the request

		RequestJwt requestJwt = RequestJwtMapper.INSTANCE.requestJwt(jwt);
		if (!requestJwt.isWriteAuthorized()) {
			throw new BadRequestException("Unauthorized, missing required scope.");
		}

		// Input Validation

		if (clientId == null || !clientId.matches(InputPatterns.CLIENT_ID)) {
			throw new BadRequestException("Invalid 'client_id' attribute format.");
		} else if (id == null || !id.matches(InputPatterns.V4UUID)) {
			throw new BadRequestException("Invalid 'id' attribute format.");
		}

		return this.clientService.deauthorizeJwtBearer(clientId, id, requestJwt);
	}

	@QueryMapping
	public ClientType client(@Argument String clientId, @AuthenticationPrincipal Jwt jwt) {

		// Authorize the request

		RequestJwt requestJwt = RequestJwtMapper.INSTANCE.requestJwt(jwt);
		if (!requestJwt.isReadAuthorized()) {
			throw new BadRequestException("Unauthorized, missing required scope.");
		}

		return this.clientService.client(clientId);
	}

	@SchemaMapping(typeName = "Client", field = "managementPermissions")
	public ClientManagementCapabilitiesType clientManagementCapabilities(ClientType client,
			@AuthenticationPrincipal Jwt jwt) {
		return this.managementCapabilitiesService.getClientManagementCapabilities(client,
				RequestJwtMapper.INSTANCE.requestJwt(jwt));
	}

	@SchemaMapping(typeName = "Client", field = "authorizationsAsSubject")
	public List<AuthorizationType> getAuthorizationsAsSubject(ClientType client) {
		return this.clientService.getAuthorizationsAsSubject(client.getClientId());
	}

	@SchemaMapping(typeName = "Client", field = "authorizationsAsAudience")
	public List<AuthorizationType> getAuthorizationsAsAudience(ClientType client) {
		return this.clientService.getAuthorizationsAsAudience(client.getClientId());
	}
}
