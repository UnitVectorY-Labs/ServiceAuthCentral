package com.unitvectory.serviceauthcentral.manage.service;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unitvectory.serviceauthcentral.datamodel.exception.ConflictException;
import com.unitvectory.serviceauthcentral.datamodel.exception.NotFoundException;
import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.manage.dto.AuthorizationType;
import com.unitvectory.serviceauthcentral.manage.dto.ClientSecretType;
import com.unitvectory.serviceauthcentral.manage.dto.ClientType;
import com.unitvectory.serviceauthcentral.manage.entropy.EntropyService;

@Service
public class DefaultClientService implements ClientService {

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private AuthorizationRepository authorizationRepository;

	@Autowired
	private EntropyService entropyService;

	@Override
	public ClientType addClient(String clientId, String description) {
		String salt = this.entropyService.generateRandom();
		this.clientRepository.putClient(clientId, description, salt);
		ClientType client = new ClientType(clientId, false, false);
		return client;
	}

	@Override
	public ClientSecretType generateClientSecret1(String clientId) {
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

	@Override
	public ClientSecretType generateClientSecret2(String clientId) {
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

	@Override
	public ClientSecretType clearClientSecret1(String clientId) {

		Client client = this.clientRepository.getClient(clientId);
		if (client == null) {
			throw new NotFoundException("clientId not found");
		}

		if (client.getClientSecret1() != null) {
			this.clientRepository.clearClientSecret1(clientId);
		}

		ClientSecretType clientSecret = new ClientSecretType(null);
		return clientSecret;
	}

	@Override
	public ClientSecretType clearClientSecret2(String clientId) {

		Client client = this.clientRepository.getClient(clientId);
		if (client == null) {
			throw new NotFoundException("clientId not found");
		}

		if (client.getClientSecret2() != null) {
			this.clientRepository.clearClientSecret2(clientId);
		}

		ClientSecretType clientSecret = new ClientSecretType(null);
		return clientSecret;
	}

	@Override
	public ClientType client(String clientId) {
		Client client = this.clientRepository.getClient(clientId);
		if (client == null) {
			return null;
		}

		return new ClientType(client);
	}

	@Override
	public List<AuthorizationType> getAuthorizationsAsSubject(String clientId) {
		return StreamSupport
				.stream(Spliterators.spliteratorUnknownSize(authorizationRepository.getAuthorizationBySubject(clientId),
						Spliterator.ORDERED), false)
				.map(AuthorizationType::new).collect(Collectors.toList());
	}

	@Override
	public List<AuthorizationType> getAuthorizationsAsAudience(String clientId) {
		return StreamSupport
				.stream(Spliterators.spliteratorUnknownSize(
						authorizationRepository.getAuthorizationByAudience(clientId), Spliterator.ORDERED), false)
				.map(AuthorizationType::new).collect(Collectors.toList());
	}
}
