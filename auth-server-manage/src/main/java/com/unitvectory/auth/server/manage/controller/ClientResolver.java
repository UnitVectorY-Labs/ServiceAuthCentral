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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import com.unitvectory.auth.server.manage.dto.AuthorizationType;
import com.unitvectory.auth.server.manage.dto.ClientManagementCapabilitiesType;
import com.unitvectory.auth.server.manage.dto.ClientSecretType;
import com.unitvectory.auth.server.manage.dto.ClientType;
import com.unitvectory.auth.server.manage.dto.ResponseType;
import com.unitvectory.auth.server.manage.dto.ClientManagementCapabilitiesType.ClientManagementCapabilitiesTypeBuilder;
import com.unitvectory.auth.server.manage.service.ClientService;

/**
 * The GraphQL Client Resolver
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Controller
public class ClientResolver {

	@Value("${sac.issuer}")
	private String issuer;

	@Autowired
	private ClientService clientService;

	@MutationMapping
	public ClientType addClient(@Argument String clientId, @Argument String description) {
		return this.clientService.addClient(clientId, description);
	}

	@MutationMapping
	public ResponseType deleteClient(@Argument String clientId) {
		return this.clientService.deleteClient(clientId);
	}

	@MutationMapping
	public ClientSecretType generateClientSecret1(@Argument String clientId) {
		return this.clientService.generateClientSecret1(clientId);
	}

	@MutationMapping
	public ClientSecretType generateClientSecret2(@Argument String clientId) {
		return this.clientService.generateClientSecret2(clientId);
	}

	@MutationMapping
	public ClientSecretType clearClientSecret1(@Argument String clientId) {
		return this.clientService.clearClientSecret1(clientId);
	}

	@MutationMapping
	public ClientSecretType clearClientSecret2(@Argument String clientId) {
		return this.clientService.clearClientSecret2(clientId);
	}

	@MutationMapping
	public ResponseType authorizeJwtBearer(@Argument String clientId, @Argument String jwksUrl,
			@Argument String iss, @Argument String sub, @Argument String aud) {
		return this.clientService.authorizeJwtBearer(clientId, jwksUrl, iss, sub, aud);
	}

	@MutationMapping
	public ResponseType deauthorizeJwtBearer(@Argument String clientId, @Argument String id) {
		return this.clientService.deauthorizeJwtBearer(clientId, id);
	}

	@QueryMapping
	public ClientType client(@Argument String clientId) {
		return this.clientService.client(clientId);
	}

	@SchemaMapping(typeName = "Client", field = "managementPermissions")
	public ClientManagementCapabilitiesType clientManagementCapabilities(ClientType client,
			@AuthenticationPrincipal Jwt jwt) {
		ClientManagementCapabilitiesTypeBuilder builder =
				ClientManagementCapabilitiesType.builder();

		boolean canDeleteClient = true;
		boolean canAddClientSecret = true;
		boolean canDeleteClientSecret = true;
		boolean canAddClientAuthorization = true;
		boolean canAddAuthorization = true;
		boolean canDeleteAuthorization = true;

		// User records are limited in what they can do
		if ("USER".equals(client.getClientType())) {
			canAddClientSecret = false;
			canDeleteClientSecret = false;
			canAddClientAuthorization = false;
			canAddAuthorization = false;
			canDeleteAuthorization = false;
		}

		// The Issuer application is highly limited as well
		if (this.issuer.equals(client.getClientId())) {
			canDeleteClient = false;
			canAddClientSecret = false;
			canDeleteClientSecret = false;
			canAddClientAuthorization = false;
			canAddAuthorization = false;
			canDeleteAuthorization = false;
		}

		// If the subject of the JWT matches the clientId then it can't be deleted
		if (jwt.getSubject().equals(client.getClientId())) {
			canDeleteClient = false;
		}

		builder.canDeleteClient(canDeleteClient);
		builder.canAddClientSecret(canAddClientSecret);
		builder.canDeleteClientSecret(canDeleteClientSecret);
		builder.canAddClientAuthorization(canAddClientAuthorization);
		builder.canAddAuthorization(canAddAuthorization);
		builder.canDeleteAuthorization(canDeleteAuthorization);

		return builder.build();
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
