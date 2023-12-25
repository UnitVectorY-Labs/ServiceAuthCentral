package com.unitvectory.serviceauthcentral.manage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import com.unitvectory.serviceauthcentral.datamodel.model.Authorization;
import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.manage.dto.AuthorizationType;
import com.unitvectory.serviceauthcentral.manage.dto.ClientType;

@Controller
public class AuthorizationResolver {

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private AuthorizationRepository authorizationRepository;

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
