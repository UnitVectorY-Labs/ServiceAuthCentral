package com.unitvectory.serviceauthcentral.manage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import com.unitvectory.serviceauthcentral.manage.dto.AuthorizationType;
import com.unitvectory.serviceauthcentral.manage.dto.ClientType;
import com.unitvectory.serviceauthcentral.manage.dto.ResponseType;
import com.unitvectory.serviceauthcentral.manage.service.AuthorizationService;

@Controller
public class AuthorizationResolver {

	@Autowired
	private AuthorizationService authorizationService;

	@MutationMapping
	public ResponseType authorize(@Argument String subject, @Argument String audience) {
		return this.authorizationService.authorize(subject, audience);
	}

	@MutationMapping
	public ResponseType deauthorize(@Argument String subject, @Argument String audience) {
		return this.authorizationService.deauthorize(subject, audience);
	}

	@QueryMapping
	public AuthorizationType authorization(@Argument String id) {
		return this.authorizationService.authorization(id);
	}

	@SchemaMapping(typeName = "Authorization", field = "subject")
	public ClientType subject(AuthorizationType authorization) {
		return this.authorizationService.subject(authorization.getSubjectId());
	}

	@SchemaMapping(typeName = "Authorization", field = "audience")
	public ClientType audience(AuthorizationType authorization) {
		return this.authorizationService.audience(authorization.getAudienceId());
	}
}
