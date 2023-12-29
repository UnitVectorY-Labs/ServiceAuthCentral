package com.unitvectory.serviceauthcentral.manage.service;

import com.unitvectory.serviceauthcentral.manage.dto.AuthorizationType;
import com.unitvectory.serviceauthcentral.manage.dto.ClientType;
import com.unitvectory.serviceauthcentral.manage.dto.ResponseType;

public interface AuthorizationService {

	ResponseType authorize(String subject, String audience);

	ResponseType deauthorize(String subject, String audience);

	AuthorizationType authorization(String id);

	ClientType subject(String subjectId);

	ClientType audience(String audienceId);
}
