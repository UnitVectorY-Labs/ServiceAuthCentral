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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import com.unitvectory.serviceauthcentral.datamodel.model.ClientSummaryConnection;
import com.unitvectory.serviceauthcentral.server.manage.dto.RequestJwt;
import com.unitvectory.serviceauthcentral.server.manage.mapper.RequestJwtMapper;
import com.unitvectory.serviceauthcentral.server.manage.service.ClientService;
import com.unitvectory.serviceauthcentral.util.exception.BadRequestException;

/**
 * The GraphQL Clients Resolver
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Controller
public class ClientsResolver {

	@Autowired
	private ClientService clientService;

	@QueryMapping
	public ClientSummaryConnection clients(@Argument Integer first, @Argument String after,
			@Argument Integer last, @Argument String before, @AuthenticationPrincipal Jwt jwt) {

		// Authorize the request

		RequestJwt requestJwt = RequestJwtMapper.INSTANCE.requestJwt(jwt);
		if (!requestJwt.isReadAuthorized()) {
			throw new BadRequestException("Unauthorized, missing required scope.");
		}

		return this.clientService.getClients(first, after, last, before);
	}
}
