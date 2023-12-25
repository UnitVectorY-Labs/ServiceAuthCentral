package com.unitvectory.serviceauthcentral.manage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.unitvectory.serviceauthcentral.datamodel.exception.NotFoundException;
import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.manage.dto.ClientType;

@Controller
public class ClientResolver {

	@Autowired
	private ClientRepository clientRepository;

	@QueryMapping
	public ClientType client(@Argument String clientId) {
		Client client = this.clientRepository.getClient(clientId);
		if (client == null) {
			throw new NotFoundException();
		}

		return new ClientType(client);
	}
}
