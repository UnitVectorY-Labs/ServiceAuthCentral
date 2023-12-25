package com.unitvectory.serviceauthcentral.datamodel.repository;

import java.util.Iterator;

import com.unitvectory.serviceauthcentral.datamodel.model.Authorization;

public interface AuthorizationRepository {

	Authorization getAuthorization(String subject, String audience);

	Iterator<Authorization> getAuthorizationBySubject(String subject);

	Iterator<Authorization> getAuthorizationByAudience(String audience);

	void authorize(String subject, String audience);

	void deauthorize(String subject, String audience);
}
