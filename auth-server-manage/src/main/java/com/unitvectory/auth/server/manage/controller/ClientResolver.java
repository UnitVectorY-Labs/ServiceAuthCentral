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
package com.unitvectory.auth.server.manage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import com.unitvectory.auth.server.manage.dto.AuthorizationType;
import com.unitvectory.auth.server.manage.dto.ClientManagementCapabilitiesType;
import com.unitvectory.auth.server.manage.dto.ClientScopeType;
import com.unitvectory.auth.server.manage.dto.ClientSecretType;
import com.unitvectory.auth.server.manage.dto.ClientType;
import com.unitvectory.auth.server.manage.dto.ResponseType;
import com.unitvectory.auth.server.manage.mapper.RequestJwtMapper;
import com.unitvectory.auth.server.manage.service.ClientService;
import com.unitvectory.auth.server.manage.service.ManagementCapabilitiesService;

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
			@Argument List<ClientScopeType> availableScopes) {
		return this.clientService.addClient(clientId, description, availableScopes);
	}

	@MutationMapping
	public ResponseType deleteClient(@Argument String clientId, @AuthenticationPrincipal Jwt jwt) {
		return this.clientService.deleteClient(clientId, RequestJwtMapper.INSTANCE.requestJwt(jwt));
	}

	@MutationMapping
	public ClientSecretType generateClientSecret1(@Argument String clientId,
			@AuthenticationPrincipal Jwt jwt) {
		return this.clientService.generateClientSecret1(clientId,
				RequestJwtMapper.INSTANCE.requestJwt(jwt));
	}

	@MutationMapping
	public ClientSecretType generateClientSecret2(@Argument String clientId,
			@AuthenticationPrincipal Jwt jwt) {
		return this.clientService.generateClientSecret2(clientId,
				RequestJwtMapper.INSTANCE.requestJwt(jwt));
	}

	@MutationMapping
	public ClientSecretType clearClientSecret1(@Argument String clientId,
			@AuthenticationPrincipal Jwt jwt) {
		return this.clientService.clearClientSecret1(clientId,
				RequestJwtMapper.INSTANCE.requestJwt(jwt));
	}

	@MutationMapping
	public ClientSecretType clearClientSecret2(@Argument String clientId,
			@AuthenticationPrincipal Jwt jwt) {
		return this.clientService.clearClientSecret2(clientId,
				RequestJwtMapper.INSTANCE.requestJwt(jwt));
	}

	@MutationMapping
	public ResponseType authorizeJwtBearer(@Argument String clientId, @Argument String jwksUrl,
			@Argument String iss, @Argument String sub, @Argument String aud,
			@AuthenticationPrincipal Jwt jwt) {
		return this.clientService.authorizeJwtBearer(clientId, jwksUrl, iss, sub, aud,
				RequestJwtMapper.INSTANCE.requestJwt(jwt));
	}

	@MutationMapping
	public ResponseType deauthorizeJwtBearer(@Argument String clientId, @Argument String id,
			@AuthenticationPrincipal Jwt jwt) {
		return this.clientService.deauthorizeJwtBearer(clientId, id,
				RequestJwtMapper.INSTANCE.requestJwt(jwt));
	}

	@QueryMapping
	public ClientType client(@Argument String clientId) {
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
