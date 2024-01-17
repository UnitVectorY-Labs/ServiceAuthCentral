package com.unitvectory.auth.server.manage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.unitvectory.auth.datamodel.model.ClientSummaryConnection;
import com.unitvectory.auth.server.manage.service.ClientService;

@Controller
public class ClientsResolver {

	@Autowired
	private ClientService clientService;

	@QueryMapping
	public ClientSummaryConnection clients(@Argument Integer first, @Argument String after,
			@Argument Integer last, @Argument String before) {
		return this.clientService.getClients(first, after, last, before);
	}
}
