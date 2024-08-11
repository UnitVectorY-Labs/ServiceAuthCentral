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
package com.unitvectory.serviceauthcentral.server.manage.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import com.unitvectory.serviceauthcentral.common.InputPatterns;
import com.unitvectory.serviceauthcentral.server.manage.dto.AuthorizationType;
import com.unitvectory.serviceauthcentral.server.manage.dto.ClientType;
import com.unitvectory.serviceauthcentral.server.manage.dto.ResponseType;
import com.unitvectory.serviceauthcentral.server.manage.mapper.RequestJwtMapper;
import com.unitvectory.serviceauthcentral.server.manage.service.AuthorizationService;
import com.unitvectory.serviceauthcentral.util.exception.BadRequestException;

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
			@Argument List<String> authorizedScopes, @AuthenticationPrincipal Jwt jwt) {

		// Input Validation

		if (subject == null || !subject.matches(InputPatterns.CLIENT_ID)) {
			throw new BadRequestException("Invalid 'subject' attribute format.");
		} else if (audience == null || !audience.matches(InputPatterns.CLIENT_ID)) {
			throw new BadRequestException("Invalid 'audience' attribute format.");
		}

		if (authorizedScopes != null) {
			for (String scope : authorizedScopes) {
				if (!scope.matches(InputPatterns.SCOPE)) {
					throw new BadRequestException("Invalid 'authorizedScopes' attribute format.");
				}
			}
		} else {
			authorizedScopes = new ArrayList<>();
		}

		return this.authorizationService.authorize(subject, audience, authorizedScopes,
				RequestJwtMapper.INSTANCE.requestJwt(jwt));
	}

	@MutationMapping
	public ResponseType deauthorize(@Argument String subject, @Argument String audience,
			@AuthenticationPrincipal Jwt jwt) {

		// Input Validation

		if (subject == null || !subject.matches(InputPatterns.CLIENT_ID)) {
			throw new BadRequestException("Invalid 'subject' attribute format.");
		} else if (audience == null || !audience.matches(InputPatterns.CLIENT_ID)) {
			throw new BadRequestException("Invalid 'audience' attribute format.");
		}

		return this.authorizationService.deauthorize(subject, audience,
				RequestJwtMapper.INSTANCE.requestJwt(jwt));
	}

	@MutationMapping
	public ResponseType authorizeAddScope(@Argument String subject, @Argument String audience,
			@Argument String authorizedScope, @AuthenticationPrincipal Jwt jwt) {

		// Input Validation

		if (subject == null || !subject.matches(InputPatterns.CLIENT_ID)) {
			throw new BadRequestException("Invalid 'subject' attribute format.");
		} else if (audience == null || !audience.matches(InputPatterns.CLIENT_ID)) {
			throw new BadRequestException("Invalid 'audience' attribute format.");
		} else if (!authorizedScope.matches(InputPatterns.SCOPE)) {
			throw new BadRequestException("Invalid 'authorizedScope' attribute format.");
		}

		return this.authorizationService.authorizeAddScope(subject, audience, authorizedScope,
				RequestJwtMapper.INSTANCE.requestJwt(jwt));
	}

	@MutationMapping
	public ResponseType authorizeRemoveScope(@Argument String subject, @Argument String audience,
			@Argument String authorizedScope, @AuthenticationPrincipal Jwt jwt) {

		// Input Validation

		if (subject == null || !subject.matches(InputPatterns.CLIENT_ID)) {
			throw new BadRequestException("Invalid 'subject' attribute format.");
		} else if (audience == null || !audience.matches(InputPatterns.CLIENT_ID)) {
			throw new BadRequestException("Invalid 'audience' attribute format.");
		} else if (!authorizedScope.matches(InputPatterns.SCOPE)) {
			throw new BadRequestException("Invalid 'authorizedScope' attribute format.");
		}

		return this.authorizationService.authorizeRemoveScope(subject, audience, authorizedScope,
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
