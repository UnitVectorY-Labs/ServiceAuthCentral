package com.unitvectory.serviceauthcentral.manage.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.unitvectory.serviceauthcentral.datamodel.model.Authorization;
import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.manage.dto.AuthorizationType;
import com.unitvectory.serviceauthcentral.manage.dto.ClientType;
import com.unitvectory.serviceauthcentral.manage.dto.ResponseType;

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

		return new AuthorizationType(authorization);
	}

	@Override
	public ClientType subject(String subjectId) {
		return new ClientType(this.clientRepository.getClient(subjectId));
	}

	@Override
	public ClientType audience(String audienceId) {
		return new ClientType(this.clientRepository.getClient(audienceId));
	}
}
