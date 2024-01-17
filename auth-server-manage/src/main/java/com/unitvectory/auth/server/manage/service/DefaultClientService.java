package com.unitvectory.auth.server.manage.service;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unitvectory.auth.common.service.entropy.EntropyService;
import com.unitvectory.auth.datamodel.model.Authorization;
import com.unitvectory.auth.datamodel.model.Client;
import com.unitvectory.auth.datamodel.model.ClientJwtBearer;
import com.unitvectory.auth.datamodel.model.ClientSummaryConnection;
import com.unitvectory.auth.datamodel.repository.AuthorizationRepository;
import com.unitvectory.auth.datamodel.repository.ClientRepository;
import com.unitvectory.auth.server.manage.dto.AuthorizationType;
import com.unitvectory.auth.server.manage.dto.ClientSecretType;
import com.unitvectory.auth.server.manage.dto.ClientType;
import com.unitvectory.auth.server.manage.dto.ResponseType;
import com.unitvectory.auth.server.manage.mapper.AuthorizationMapper;
import com.unitvectory.auth.server.manage.mapper.ClientMapper;
import com.unitvectory.auth.util.exception.ConflictException;
import com.unitvectory.auth.util.exception.NotFoundException;

@Service
public class DefaultClientService implements ClientService {

	private static int LENGTH = 32;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private AuthorizationRepository authorizationRepository;

	@Autowired
	private EntropyService entropyService;

	@Override
	public ClientSummaryConnection getClients(Integer first, String after, Integer last,
			String before) {
		// Validate that either first or last is provided, but not both
		if ((first == null && last == null) || (first != null && last != null)) {
			throw new IllegalArgumentException(
					"Either 'first' or 'last' must be provided, but not both.");
		}

		// Validate that after is provided only if first is set
		if (after != null && first == null) {
			throw new IllegalArgumentException(
					"The 'after' parameter can only be provided if 'first' is set.");
		}

		// Validate that before is provided only if last is set
		if (before != null && last == null) {
			throw new IllegalArgumentException(
					"The 'before' parameter can only be provided if 'last' is set.");
		}

		// Check that first or last is between 1 and 10 if set
		if ((first != null && (first < 1 || first > 10))
				|| (last != null && (last < 1 || last > 10))) {
			throw new IllegalArgumentException("'first' or 'last' must be between 1 and 10.");
		}

		// Call the repository method
		ClientSummaryConnection clientSummaryPage =
				this.clientRepository.getClients(first, after, last, before);
		return clientSummaryPage;
	}

	@Override
	public ClientType addClient(String clientId, String description) {
		String salt = this.entropyService.randomAlphaNumeric(LENGTH);
		this.clientRepository.putClient(clientId, description, salt);
		ClientType client = ClientType.builder().clientId(clientId).clientSecret1Set(false)
				.clientSecret2Set(false).build();
		return client;
	}

	@Override
	public ResponseType deleteClient(String clientId) {

		// Delete all of the authorization records where the clientId is the audience
		Iterator<Authorization> aud =
				this.authorizationRepository.getAuthorizationByAudience(clientId);
		while (aud.hasNext()) {
			Authorization auth = aud.next();
			this.authorizationRepository.deleteAuthorization(auth.getDocumentId());
		}

		// Delete all of the authorization records where the clientId is the subject
		Iterator<Authorization> sub =
				this.authorizationRepository.getAuthorizationBySubject(clientId);
		while (sub.hasNext()) {
			Authorization auth = sub.next();
			this.authorizationRepository.deleteAuthorization(auth.getDocumentId());
		}

		// Now delete the client
		this.clientRepository.deleteClient(clientId);

		// All done
		return ResponseType.builder().success(true).build();
	}

	@Override
	public ClientSecretType generateClientSecret1(String clientId) {
		String secret = this.entropyService.randomAlphaNumeric(LENGTH);

		Client client = this.clientRepository.getClient(clientId);
		if (client == null) {
			throw new NotFoundException("clientId not found");
		}

		if (client.getClientSecret1() != null) {
			throw new ConflictException("clientId clientSecret1 already set");
		}

		String hashedSecret = client.hashSecret(secret);
		this.clientRepository.saveClientSecret1(clientId, hashedSecret);

		ClientSecretType clientSecret = ClientSecretType.builder().clientSecret(secret).build();
		return clientSecret;
	}

	@Override
	public ClientSecretType generateClientSecret2(String clientId) {
		String secret = this.entropyService.randomAlphaNumeric(LENGTH);

		Client client = this.clientRepository.getClient(clientId);
		if (client == null) {
			throw new NotFoundException("clientId not found");
		}

		if (client.getClientSecret2() != null) {
			throw new ConflictException("clientId clientSecret2 already set");
		}

		String hashedSecret = client.hashSecret(secret);
		this.clientRepository.saveClientSecret2(clientId, hashedSecret);

		ClientSecretType clientSecret = ClientSecretType.builder().clientSecret(secret).build();
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

		ClientSecretType clientSecret = ClientSecretType.builder().build();
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

		ClientSecretType clientSecret = ClientSecretType.builder().build();
		return clientSecret;
	}

	@Override
	public ClientType client(String clientId) {
		Client client = this.clientRepository.getClient(clientId);
		if (client == null) {
			return null;
		}

		return ClientMapper.INSTANCE.clientToClientType(client);
	}

	@Override
	public List<AuthorizationType> getAuthorizationsAsSubject(String clientId) {
		return StreamSupport
				.stream(Spliterators.spliteratorUnknownSize(
						authorizationRepository.getAuthorizationBySubject(clientId),
						Spliterator.ORDERED), false)
				.map(AuthorizationMapper.INSTANCE::authorizationToAuthorizationType)
				.collect(Collectors.toList());
	}

	@Override
	public List<AuthorizationType> getAuthorizationsAsAudience(String clientId) {
		return StreamSupport
				.stream(Spliterators.spliteratorUnknownSize(
						authorizationRepository.getAuthorizationByAudience(clientId),
						Spliterator.ORDERED), false)
				.map(AuthorizationMapper.INSTANCE::authorizationToAuthorizationType)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseType authorizeJwtBearer(String clientId, String jwksUrl, String iss, String sub,
			String aud) {
		Client client = this.clientRepository.getClient(clientId);
		if (client == null) {
			return ResponseType.builder().success(false).build();
		}

		for (ClientJwtBearer cjb : client.getJwtBearer()) {
			if (cjb.matches(jwksUrl, iss, sub, aud)) {
				// Already matches, fail
				return ResponseType.builder().success(false).build();
			}
		}

		String id = this.entropyService.generateUuid();
		this.clientRepository.addAuthorizedJwt(clientId, id, jwksUrl, iss, sub, aud);
		return ResponseType.builder().success(true).build();
	}

	@Override
	public ResponseType deauthorizeJwtBearer(String clientId, String id) {
		Client client = this.clientRepository.getClient(clientId);
		if (client == null) {
			return ResponseType.builder().success(false).build();
		}

		boolean matches = false;
		for (ClientJwtBearer cjb : client.getJwtBearer()) {
			if (id != null && id.equals(cjb.getId())) {
				matches = true;
				break;
			}
		}

		if (!matches) {
			return ResponseType.builder().success(false).build();
		}

		this.clientRepository.removeAuthorizedJwt(clientId, id);
		return ResponseType.builder().success(true).build();
	}
}
