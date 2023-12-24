package com.unitvectory.serviceauthcentral.datamodel.repository;

import com.unitvectory.serviceauthcentral.datamodel.model.Client;

public interface ClientRepository {

	Client getClient(String clientId);

}
