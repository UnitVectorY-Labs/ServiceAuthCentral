package com.unitvectory.serviceauthcentral.manage.controller;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import com.unitvectory.serviceauthcentral.datamodel.exception.ConflictException;
import com.unitvectory.serviceauthcentral.datamodel.exception.NotFoundException;
import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.manage.dto.AuthorizationType;
import com.unitvectory.serviceauthcentral.manage.dto.ClientSecretType;
import com.unitvectory.serviceauthcentral.manage.dto.ClientType;
import com.unitvectory.serviceauthcentral.manage.entropy.EntropyService;

@Controller
public class ClientResolver {

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private AuthorizationRepository authorizationRepository;

	@Autowired
	private EntropyService entropyService;

	@MutationMapping
	public ClientType addClient(@Argument String clientId, @Argument String description) {
		String salt = this.entropyService.generateRandom();
		this.clientRepository.putClient(clientId, description, salt);
		ClientType client = new ClientType(clientId, false, false);
		return client;
	}

	@MutationMapping
	public ClientSecretType generateClientSecret1(@Argument String clientId) {
		String secret = this.entropyService.generateRandom();

		Client client = this.clientRepository.getClient(clientId);
		if (client == null) {
			throw new NotFoundException("clientId not found");
		}

		if (client.getClientSecret1() != null) {
			throw new ConflictException("clientId clientSecret1 already set");
		}

		String hashedSecret = client.hashSecret(secret);
		this.clientRepository.saveClientSecret1(clientId, hashedSecret);

		ClientSecretType clientSecret = new ClientSecretType(secret);
		return clientSecret;
	}

	@MutationMapping
	public ClientSecretType generateClientSecret2(@Argument String clientId) {
		String secret = this.entropyService.generateRandom();

		Client client = this.clientRepository.getClient(clientId);
		if (client == null) {
			throw new NotFoundException("clientId not found");
		}

		if (client.getClientSecret2() != null) {
			throw new ConflictException("clientId clientSecret2 already set");
		}

		String hashedSecret = client.hashSecret(secret);
		this.clientRepository.saveClientSecret2(clientId, hashedSecret);

		ClientSecretType clientSecret = new ClientSecretType(secret);
		return clientSecret;
	}

	@MutationMapping
	public ClientType clearClientSecret1(@Argument String clientId) {

		Client client = this.clientRepository.getClient(clientId);
		if (client == null) {
			throw new NotFoundException("clientId not found");
		}

		if (client.getClientSecret1() != null) {
			this.clientRepository.clearClientSecret1(clientId);
		}

		ClientType clientType = new ClientType(client);
		clientType.setClientSecret1Set(false);
		return clientType;
	}

	@MutationMapping
	public ClientType clearClientSecret2(@Argument String clientId) {

		Client client = this.clientRepository.getClient(clientId);
		if (client == null) {
			throw new NotFoundException("clientId not found");
		}

		if (client.getClientSecret2() != null) {
			this.clientRepository.clearClientSecret2(clientId);
		}

		ClientType clientType = new ClientType(client);
		clientType.setClientSecret2Set(false);
		return clientType;
	}

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
