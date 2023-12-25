package com.unitvectory.serviceauthcentral.manage.dto;

import com.unitvectory.serviceauthcentral.datamodel.model.Client;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientType {

	private String clientId;

	private boolean clientSecret1Set;

	private boolean clientSecret2Set;

	public ClientType(Client client) {
		this.clientId = client.getClientId();
		this.clientSecret1Set = client.getClientSecret1() != null;
		this.clientSecret2Set = client.getClientSecret2() != null;
	}

}
