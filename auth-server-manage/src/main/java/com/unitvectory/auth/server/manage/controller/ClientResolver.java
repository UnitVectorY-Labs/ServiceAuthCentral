package com.unitvectory.auth.server.manage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import com.unitvectory.auth.server.manage.dto.AuthorizationType;
import com.unitvectory.auth.server.manage.dto.ClientSecretType;
import com.unitvectory.auth.server.manage.dto.ClientType;
import com.unitvectory.auth.server.manage.dto.ResponseType;
import com.unitvectory.auth.server.manage.service.ClientService;

@Controller
public class ClientResolver {

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

	@SchemaMapping(typeName = "Client", field = "authorizationsAsSubject")
	public List<AuthorizationType> getAuthorizationsAsSubject(ClientType client) {
		return this.clientService.getAuthorizationsAsSubject(client.getClientId());
	}

	@SchemaMapping(typeName = "Client", field = "authorizationsAsAudience")
	public List<AuthorizationType> getAuthorizationsAsAudience(ClientType client) {
		return this.clientService.getAuthorizationsAsAudience(client.getClientId());
	}
}
