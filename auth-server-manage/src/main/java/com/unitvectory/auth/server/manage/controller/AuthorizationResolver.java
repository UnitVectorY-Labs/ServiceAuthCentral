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
package com.unitvectory.auth.server.manage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import com.unitvectory.auth.server.manage.dto.AuthorizationType;
import com.unitvectory.auth.server.manage.dto.ClientType;
import com.unitvectory.auth.server.manage.dto.ResponseType;
import com.unitvectory.auth.server.manage.mapper.RequestJwtMapper;
import com.unitvectory.auth.server.manage.service.AuthorizationService;

/**
 * The GraphQL Authorization Resolver
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Controller
public class AuthorizationResolver {

	@Autowired
	private AuthorizationService authorizationService;

	@MutationMapping
	public ResponseType authorize(@Argument String subject, @Argument String audience,
			@AuthenticationPrincipal Jwt jwt) {
		return this.authorizationService.authorize(subject, audience,
				RequestJwtMapper.INSTANCE.requestJwt(jwt));
	}

	@MutationMapping
	public ResponseType deauthorize(@Argument String subject, @Argument String audience,
			@AuthenticationPrincipal Jwt jwt) {
		return this.authorizationService.deauthorize(subject, audience,
				RequestJwtMapper.INSTANCE.requestJwt(jwt));
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
