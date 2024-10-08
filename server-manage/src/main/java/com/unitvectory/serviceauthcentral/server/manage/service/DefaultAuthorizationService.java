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
package com.unitvectory.serviceauthcentral.server.manage.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;

import com.unitvectory.serviceauthcentral.datamodel.model.Authorization;
import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientScope;
import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.server.manage.dto.AuthorizationType;
import com.unitvectory.serviceauthcentral.server.manage.dto.ClientManagementCapabilitiesType;
import com.unitvectory.serviceauthcentral.server.manage.dto.ClientType;
import com.unitvectory.serviceauthcentral.server.manage.dto.RequestJwt;
import com.unitvectory.serviceauthcentral.server.manage.dto.ResponseType;
import com.unitvectory.serviceauthcentral.server.manage.mapper.AuthorizationMapper;
import com.unitvectory.serviceauthcentral.server.manage.mapper.ClientMapper;
import com.unitvectory.serviceauthcentral.util.exception.BadRequestException;

/**
 * The implementation of the Authorization Service
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public class DefaultAuthorizationService implements AuthorizationService {

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private AuthorizationRepository authorizationRepository;

	@Autowired
	private ManagementCapabilitiesService managementCapabilitiesService;

	@Override
	public ResponseType authorize(String subject, String audience, List<String> authorizedScopes,
			RequestJwt jwt) {
		Client subjectClient = this.clientRepository.getClient(subject);
		if (subjectClient == null) {
			return ResponseType.builder().success(false).build();
		}

		Client audienceClient = this.clientRepository.getClient(audience);
		if (audienceClient == null) {
			return ResponseType.builder().success(false).build();
		}

		ClientManagementCapabilitiesType managementCapabilities =
				this.managementCapabilitiesService.getClientManagementCapabilities(
						ClientMapper.INSTANCE.clientToClientType(audienceClient), jwt);
		if (!managementCapabilities.isCanAddAuthorization()) {
			throw new BadRequestException("The client cannot add authorization");
		}

		Set<String> uniqueScopes = new HashSet<>();
		for (String scope : authorizedScopes) {
			if (!audienceClient.hasScope(scope)) {
				throw new BadRequestException("The client does not have the scope '" + scope + "'");
			}

			if (uniqueScopes.contains(scope)) {
				throw new IllegalArgumentException("The scope '" + scope + "' is duplicated.");
			}
			uniqueScopes.add(scope);
		}

		this.authorizationRepository.authorize(subject, audience, authorizedScopes);
		return ResponseType.builder().success(true).build();
	}

	@Override
	public ResponseType deauthorize(String subject, String audience, RequestJwt jwt) {

		Client audienceClient = this.clientRepository.getClient(audience);
		if (audienceClient == null) {
			return ResponseType.builder().success(false).build();
		}

		ClientManagementCapabilitiesType managementCapabilities =
				this.managementCapabilitiesService.getClientManagementCapabilities(
						ClientMapper.INSTANCE.clientToClientType(audienceClient), jwt);
		if (!managementCapabilities.isCanDeleteAuthorization()) {
			throw new BadRequestException("The client cannot delete authorization");
		}

		this.authorizationRepository.deauthorize(subject, audience);
		return ResponseType.builder().success(true).build();
	}

	@Override
	public AuthorizationType authorization(String id) {
		Authorization authorization = this.authorizationRepository.getAuthorization(id);
		if (authorization == null) {
			return null;
		}

		return AuthorizationMapper.INSTANCE.authorizationToAuthorizationType(authorization);
	}

	@Override
	public ClientType subject(String subjectId) {
		Client client = this.clientRepository.getClient(subjectId);
		return ClientMapper.INSTANCE.clientToClientType(client);
	}

	@Override
	public ClientType audience(String audienceId) {
		Client client = this.clientRepository.getClient(audienceId);
		return ClientMapper.INSTANCE.clientToClientType(client);
	}

	@Override
	public ResponseType authorizeAddScope(String subject, String audience, String authorizedScope,
			RequestJwt jwt) {

		Authorization authorization =
				this.authorizationRepository.getAuthorization(subject, audience);
		if (authorization == null) {
			throw new BadRequestException("The authorization does not exist");
		}

		Client audienceClient = this.clientRepository.getClient(audience);
		if (audienceClient == null) {
			return ResponseType.builder().success(false).build();
		}

		ClientManagementCapabilitiesType managementCapabilities =
				this.managementCapabilitiesService.getClientManagementCapabilities(
						ClientMapper.INSTANCE.clientToClientType(audienceClient), jwt);
		if (!managementCapabilities.isCanAuthorizeAddScope()) {
			throw new BadRequestException("The client cannot add scope from authorization");
		}

		if (authorization.getAuthorizedScopes().contains(authorizedScope)) {
			// The scope is already authorized
			return ResponseType.builder().success(true).build();
		}

		boolean scopeAllowed = false;
		for (ClientScope scope : audienceClient.getAvailableScopes()) {
			if (scope.getScope().equals(authorizedScope)) {
				scopeAllowed = true;
				break;
			}
		}

		if (!scopeAllowed) {
			throw new BadRequestException("The scope is not authorized");
		}



		this.authorizationRepository.authorizeAddScope(subject, audience, authorizedScope);


		return ResponseType.builder().success(true).build();
	}

	@Override
	public ResponseType authorizeRemoveScope(String subject, String audience,
			String authorizedScope, RequestJwt jwt) {

		Authorization authorization =
				this.authorizationRepository.getAuthorization(subject, audience);
		if (authorization == null) {
			throw new BadRequestException("The authorization does not exist");
		}

		Client audienceClient = this.clientRepository.getClient(audience);
		if (audienceClient == null) {
			return ResponseType.builder().success(false).build();
		}

		ClientManagementCapabilitiesType managementCapabilities =
				this.managementCapabilitiesService.getClientManagementCapabilities(
						ClientMapper.INSTANCE.clientToClientType(audienceClient), jwt);
		if (!managementCapabilities.isCanAuthorizeRemoveScope()) {
			throw new BadRequestException("The client cannot remove scope from authorization");
		}

		this.authorizationRepository.authorizeRemoveScope(subject, audience, authorizedScope);

		return ResponseType.builder().success(true).build();
	}
}
