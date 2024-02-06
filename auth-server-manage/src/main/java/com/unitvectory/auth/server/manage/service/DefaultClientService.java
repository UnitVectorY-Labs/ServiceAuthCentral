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
package com.unitvectory.auth.server.manage.service;

import java.util.ArrayList;
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
import com.unitvectory.auth.datamodel.model.ClientScope;
import com.unitvectory.auth.datamodel.model.ClientSummaryConnection;
import com.unitvectory.auth.datamodel.repository.AuthorizationRepository;
import com.unitvectory.auth.datamodel.repository.ClientRepository;
import com.unitvectory.auth.server.manage.dto.AuthorizationType;
import com.unitvectory.auth.server.manage.dto.ClientManagementCapabilitiesType;
import com.unitvectory.auth.server.manage.dto.ClientScopeType;
import com.unitvectory.auth.server.manage.dto.ClientSecretType;
import com.unitvectory.auth.server.manage.dto.ClientType;
import com.unitvectory.auth.server.manage.dto.RequestJwt;
import com.unitvectory.auth.server.manage.dto.ResponseType;
import com.unitvectory.auth.server.manage.mapper.AuthorizationMapper;
import com.unitvectory.auth.server.manage.mapper.ClientMapper;
import com.unitvectory.auth.util.exception.BadRequestException;
import com.unitvectory.auth.util.exception.ConflictException;
import com.unitvectory.auth.util.exception.NotFoundException;

/**
 * The implementation of the Client Service
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Service
public class DefaultClientService implements ClientService {

	private static int LENGTH = 32;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private AuthorizationRepository authorizationRepository;

	@Autowired
	private EntropyService entropyService;

	@Autowired
	private ManagementCapabilitiesService managementCapabilitiesService;

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
	public ClientType addClient(String clientId, String description,
			List<ClientScopeType> availableScopes) {

		// Application clientIds cannot start with 'user:'
		if (clientId.toLowerCase().startsWith("user:")) {
			throw new IllegalArgumentException(
					"Application 'clientId' cannot start with 'user:' as this is reserved for user accounts.");
		} else if (clientId.toLowerCase().startsWith("provider:")) {
			throw new IllegalArgumentException(
					"Application 'clientId' cannot start with 'provider:' as this is reserved for login providers.");
		}

		List<ClientScope> availableScopesList = new ArrayList<>();
		if (availableScopes != null) {
			for (ClientScopeType scope : availableScopes) {
				availableScopesList.add(scope);
			}
		}


		String salt = this.entropyService.randomAlphaNumeric(LENGTH);
		this.clientRepository.putClient(clientId, description, salt,
				com.unitvectory.auth.datamodel.model.ClientType.APPLICATION, availableScopesList);
		ClientType client = ClientType.builder().clientId(clientId).description(description)
				.availableScopes(availableScopes).clientSecret1Set(false).clientSecret2Set(false)
				.build();
		return client;
	}

	@Override
	public ResponseType deleteClient(String clientId, RequestJwt jwt) {

		Client client = this.clientRepository.getClient(clientId);
		if (client == null) {
			throw new NotFoundException("clientId not found");
		}

		ClientManagementCapabilitiesType managementCapabilities =
				this.managementCapabilitiesService.getClientManagementCapabilities(
						ClientMapper.INSTANCE.clientToClientType(client), jwt);
		if (!managementCapabilities.isCanDeleteClient()) {
			throw new BadRequestException("The client cannot be deleted");
		}

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
	public ClientSecretType generateClientSecret1(String clientId, RequestJwt jwt) {
		String secret = this.entropyService.randomAlphaNumeric(LENGTH);

		Client client = this.clientRepository.getClient(clientId);
		if (client == null) {
			throw new NotFoundException("clientId not found");
		}

		ClientManagementCapabilitiesType managementCapabilities =
				this.managementCapabilitiesService.getClientManagementCapabilities(
						ClientMapper.INSTANCE.clientToClientType(client), jwt);
		if (!managementCapabilities.isCanAddClientSecret()) {
			throw new BadRequestException("The client cannot have a client secret added");
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
	public ClientSecretType generateClientSecret2(String clientId, RequestJwt jwt) {
		String secret = this.entropyService.randomAlphaNumeric(LENGTH);

		Client client = this.clientRepository.getClient(clientId);
		if (client == null) {
			throw new NotFoundException("clientId not found");
		}

		ClientManagementCapabilitiesType managementCapabilities =
				this.managementCapabilitiesService.getClientManagementCapabilities(
						ClientMapper.INSTANCE.clientToClientType(client), jwt);
		if (!managementCapabilities.isCanAddClientSecret()) {
			throw new BadRequestException("The client cannot have a client secret added");
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
	public ClientSecretType clearClientSecret1(String clientId, RequestJwt jwt) {

		Client client = this.clientRepository.getClient(clientId);
		if (client == null) {
			throw new NotFoundException("clientId not found");
		}

		ClientManagementCapabilitiesType managementCapabilities =
				this.managementCapabilitiesService.getClientManagementCapabilities(
						ClientMapper.INSTANCE.clientToClientType(client), jwt);
		if (!managementCapabilities.isCanDeleteClientSecret()) {
			throw new BadRequestException("The client cannot have a client secret deleted");
		}

		if (client.getClientSecret1() != null) {
			this.clientRepository.clearClientSecret1(clientId);
		}

		ClientSecretType clientSecret = ClientSecretType.builder().build();
		return clientSecret;
	}

	@Override
	public ClientSecretType clearClientSecret2(String clientId, RequestJwt jwt) {

		Client client = this.clientRepository.getClient(clientId);
		if (client == null) {
			throw new NotFoundException("clientId not found");
		}

		ClientManagementCapabilitiesType managementCapabilities =
				this.managementCapabilitiesService.getClientManagementCapabilities(
						ClientMapper.INSTANCE.clientToClientType(client), jwt);
		if (!managementCapabilities.isCanDeleteClientSecret()) {
			throw new BadRequestException("The client cannot have a client secret deleted");
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
			String aud, RequestJwt jwt) {
		Client client = this.clientRepository.getClient(clientId);
		if (client == null) {
			return ResponseType.builder().success(false).build();
		}

		ClientManagementCapabilitiesType managementCapabilities =
				this.managementCapabilitiesService.getClientManagementCapabilities(
						ClientMapper.INSTANCE.clientToClientType(client), jwt);
		if (!managementCapabilities.isCanAddClientAuthorization()) {
			throw new BadRequestException("The client cannot have a client authorization added");
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
	public ResponseType deauthorizeJwtBearer(String clientId, String id, RequestJwt jwt) {
		Client client = this.clientRepository.getClient(clientId);
		if (client == null) {
			return ResponseType.builder().success(false).build();
		}

		ClientManagementCapabilitiesType managementCapabilities =
				this.managementCapabilitiesService.getClientManagementCapabilities(
						ClientMapper.INSTANCE.clientToClientType(client), jwt);
		if (!managementCapabilities.isCanDeleteClientAuthorization()) {
			throw new BadRequestException("The client cannot have a client authorization deleted");
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
