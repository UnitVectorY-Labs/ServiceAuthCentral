package com.unitvectory.serviceauthcentral.manage.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.unitvectory.serviceauthcentral.manage.model.Client;

@Controller
public class ClientResolver {

	@QueryMapping
	public Client client(@Argument String clientId) {
		return Client.builder().clientId(clientId).build();
	}
}
