package com.unitvectory.auth.datamodel.repository;

import java.util.Iterator;

import com.unitvectory.auth.datamodel.model.Authorization;

public interface AuthorizationRepository {

	Authorization getAuthorization(String id);

	Authorization getAuthorization(String subject, String audience);

	Iterator<Authorization> getAuthorizationBySubject(String subject);

	Iterator<Authorization> getAuthorizationByAudience(String audience);

	void authorize(String subject, String audience);

	void deauthorize(String subject, String audience);
}