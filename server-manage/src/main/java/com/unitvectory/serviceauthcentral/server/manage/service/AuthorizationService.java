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
package com.unitvectory.serviceauthcentral.server.manage.service;

import java.util.List;
import com.unitvectory.serviceauthcentral.server.manage.dto.AuthorizationType;
import com.unitvectory.serviceauthcentral.server.manage.dto.ClientType;
import com.unitvectory.serviceauthcentral.server.manage.dto.RequestJwt;
import com.unitvectory.serviceauthcentral.server.manage.dto.ResponseType;

/**
 * The interface for the Authorization Service
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public interface AuthorizationService {

	ResponseType authorize(String subject, String audience, List<String> authorizedScopes,
			RequestJwt jwt);

	ResponseType deauthorize(String subject, String audience, RequestJwt jwt);

	AuthorizationType authorization(String id);

	ClientType subject(String subjectId);

	ClientType audience(String audienceId);

	ResponseType authorizeAddScope(String subject, String audience, String authorizedScope,
			RequestJwt jwt);

	ResponseType authorizeRemoveScope(String subject, String audience, String authorizedScope,
			RequestJwt jwt);
}
