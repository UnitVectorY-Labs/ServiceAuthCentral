package com.unitvectory.auth.server.manage.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.unitvectory.auth.datamodel.model.Authorization;
import com.unitvectory.auth.datamodel.model.Client;
import com.unitvectory.auth.datamodel.repository.AuthorizationRepository;
import com.unitvectory.auth.datamodel.repository.ClientRepository;
import com.unitvectory.auth.server.manage.dto.AuthorizationType;
import com.unitvectory.auth.server.manage.dto.ClientType;
import com.unitvectory.auth.server.manage.dto.ResponseType;
import com.unitvectory.auth.server.manage.mapper.AuthorizationMapper;
import com.unitvectory.auth.server.manage.mapper.ClientMapper;

public class DefaultAuthorizationService implements AuthorizationService {

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private AuthorizationRepository authorizationRepository;

	@Override
	public ResponseType authorize(String subject, String audience) {
		Client subjectClient = this.clientRepository.getClient(subject);
		if (subjectClient == null) {
			return ResponseType.builder().success(false).build();
		}

		Client audienceClient = this.clientRepository.getClient(audience);
		if (audienceClient == null) {
			return ResponseType.builder().success(false).build();
		}

		this.authorizationRepository.authorize(subject, audience);
		return ResponseType.builder().success(true).build();
	}

	@Override
	public ResponseType deauthorize(String subject, String audience) {
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
