package com.unitvectory.serviceauthcentral.manage.service;

import java.util.List;

import com.unitvectory.serviceauthcentral.manage.dto.AuthorizationType;
import com.unitvectory.serviceauthcentral.manage.dto.ClientSecretType;
import com.unitvectory.serviceauthcentral.manage.dto.ClientType;

public interface ClientService {

	ClientType addClient(String clientId, String description);

	ClientSecretType generateClientSecret1(String clientId);

	ClientSecretType generateClientSecret2(String clientId);

	ClientSecretType clearClientSecret1(String clientId);

	ClientSecretType clearClientSecret2(String clientId);

	ClientType client(String clientId);

	List<AuthorizationType> getAuthorizationsAsSubject(String clientId);

	List<AuthorizationType> getAuthorizationsAsAudience(String clientId);
}
