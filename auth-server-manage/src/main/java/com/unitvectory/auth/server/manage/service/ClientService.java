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
package com.unitvectory.auth.server.manage.service;

import java.util.List;

import com.unitvectory.auth.datamodel.model.ClientSummaryConnection;
import com.unitvectory.auth.server.manage.dto.AuthorizationType;
import com.unitvectory.auth.server.manage.dto.ClientSecretType;
import com.unitvectory.auth.server.manage.dto.ClientType;
import com.unitvectory.auth.server.manage.dto.ResponseType;

/**
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
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
