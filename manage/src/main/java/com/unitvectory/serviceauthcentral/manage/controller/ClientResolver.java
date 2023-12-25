package com.unitvectory.serviceauthcentral.manage.controller;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.manage.dto.AuthorizationType;
import com.unitvectory.serviceauthcentral.manage.dto.ClientType;

@Controller
public class ClientResolver {

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private AuthorizationRepository authorizationRepository;

	@QueryMapping
	public ClientType client(@Argument String clientId) {
		Client client = this.clientRepository.getClient(clientId);
		if (client == null) {
			return null;
		}

		return new ClientType(client);
	}

	@SchemaMapping(typeName = "Client", field = "authorizationsAsSubject")
	public List<AuthorizationType> getAuthorizationsAsSubject(ClientType client) {
		return StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(
						authorizationRepository.getAuthorizationBySubject(client.getClientId()), Spliterator.ORDERED),
				false).map(AuthorizationType::new).collect(Collectors.toList());
	}

	@SchemaMapping(typeName = "Client", field = "authorizationsAsAudience")
	public List<AuthorizationType> getAuthorizationsAsAudience(ClientType client) {
		return StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(
						authorizationRepository.getAuthorizationByAudience(client.getClientId()), Spliterator.ORDERED),
				false).map(AuthorizationType::new).collect(Collectors.toList());
	}
}
