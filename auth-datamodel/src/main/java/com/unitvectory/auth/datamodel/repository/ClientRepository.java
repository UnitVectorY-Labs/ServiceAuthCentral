package com.unitvectory.auth.datamodel.repository;

import com.unitvectory.auth.datamodel.model.Client;

public interface ClientRepository {

	Client getClient(String clientId);

	void putClient(String clientId, String description, String salt);

	void saveClientSecret1(String clientId, String hashedSecret);

	void saveClientSecret2(String clientId, String hashedSecret);

	void clearClientSecret1(String clientId);

	void clearClientSecret2(String clientId);

}
