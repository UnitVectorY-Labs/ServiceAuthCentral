package com.unitvectory.auth.server.manage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.unitvectory.auth.server.manage.dto.ClientSummaryType;
import com.unitvectory.auth.server.manage.service.ClientService;

@Controller
public class ClientsResolver {

	@Autowired
	private ClientService clientService;

	@QueryMapping
	public List<ClientSummaryType> clients() {
		return this.clientService.getClients();
	}
}
