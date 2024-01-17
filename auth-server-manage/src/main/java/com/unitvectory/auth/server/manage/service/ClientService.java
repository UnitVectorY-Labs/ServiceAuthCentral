package com.unitvectory.auth.server.manage.service;

import java.util.List;

import com.unitvectory.auth.datamodel.model.ClientSummaryConnection;
import com.unitvectory.auth.server.manage.dto.AuthorizationType;
import com.unitvectory.auth.server.manage.dto.ClientSecretType;
import com.unitvectory.auth.server.manage.dto.ClientType;
import com.unitvectory.auth.server.manage.dto.ResponseType;

public interface ClientService {

	ClientSummaryConnection getClients(Integer first, String after, Integer last, String before);

	ClientType addClient(String clientId, String description);

	ResponseType deleteClient(String clientId);

	ClientSecretType generateClientSecret1(String clientId);

	ClientSecretType generateClientSecret2(String clientId);

	ClientSecretType clearClientSecret1(String clientId);

	ClientSecretType clearClientSecret2(String clientId);

	ResponseType authorizeJwtBearer(String clientId, String jwksUrl, String iss, String sub,
			String aud);

	ResponseType deauthorizeJwtBearer(String clientId, String id);

	ClientType client(String clientId);

	List<AuthorizationType> getAuthorizationsAsSubject(String clientId);

	List<AuthorizationType> getAuthorizationsAsAudience(String clientId);
}
