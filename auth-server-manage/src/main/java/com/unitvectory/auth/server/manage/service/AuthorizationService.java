package com.unitvectory.auth.server.manage.service;

import com.unitvectory.auth.server.manage.dto.AuthorizationType;
import com.unitvectory.auth.server.manage.dto.ClientType;
import com.unitvectory.auth.server.manage.dto.ResponseType;

public interface AuthorizationService {

	ResponseType authorize(String subject, String audience);

	ResponseType deauthorize(String subject, String audience);

	AuthorizationType authorization(String id);

	ClientType subject(String subjectId);

	ClientType audience(String audienceId);
}
