package com.unitvectory.serviceauthcentral.manage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import com.unitvectory.serviceauthcentral.datamodel.model.Authorization;
import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.manage.dto.AuthorizationType;
import com.unitvectory.serviceauthcentral.manage.dto.ClientType;
import com.unitvectory.serviceauthcentral.manage.dto.ResponseType;

@Controller
public class AuthorizationResolver {

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private AuthorizationRepository authorizationRepository;

	@MutationMapping
	public ResponseType authorize(@Argument String subject, @Argument String audience) {
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

	@MutationMapping
	public ResponseType deauthorize(@Argument String subject, @Argument String audience) {
		this.authorizationRepository.deauthorize(subject, audience);
		return ResponseType.builder().success(true).build();
	}

	@QueryMapping
	public AuthorizationType authorization(@Argument String id) {
		Authorization authorization = this.authorizationRepository.getAuthorization(id);
		if (authorization == null) {
			return null;
		}

		return new AuthorizationType(authorization);
	}

	@SchemaMapping(typeName = "Authorization", field = "subject")
	public ClientType subject(AuthorizationType authorization) {
		return new ClientType(this.clientRepository.getClient(authorization.getSubjectId()));
	}

	@SchemaMapping(typeName = "Authorization", field = "audience")
	public ClientType audience(AuthorizationType authorization) {
		return new ClientType(this.clientRepository.getClient(authorization.getAudienceId()));
	}
}
