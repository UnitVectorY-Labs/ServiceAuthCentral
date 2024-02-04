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

import org.springframework.beans.factory.annotation.Autowired;

import com.unitvectory.auth.datamodel.model.Authorization;
import com.unitvectory.auth.datamodel.model.Client;
import com.unitvectory.auth.datamodel.repository.AuthorizationRepository;
import com.unitvectory.auth.datamodel.repository.ClientRepository;
import com.unitvectory.auth.server.manage.dto.AuthorizationType;
import com.unitvectory.auth.server.manage.dto.ClientManagementCapabilitiesType;
import com.unitvectory.auth.server.manage.dto.ClientType;
import com.unitvectory.auth.server.manage.dto.RequestJwt;
import com.unitvectory.auth.server.manage.dto.ResponseType;
import com.unitvectory.auth.server.manage.mapper.AuthorizationMapper;
import com.unitvectory.auth.server.manage.mapper.ClientMapper;
import com.unitvectory.auth.util.exception.BadRequestException;

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
	public ResponseType authorize(String subject, String audience, RequestJwt jwt) {
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

		this.authorizationRepository.authorize(subject, audience);
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
}
