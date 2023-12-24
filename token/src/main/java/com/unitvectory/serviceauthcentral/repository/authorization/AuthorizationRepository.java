package com.unitvectory.serviceauthcentral.repository.authorization;

import java.util.concurrent.ExecutionException;

import com.unitvectory.serviceauthcentral.model.AuthorizationRecord;

public interface AuthorizationRepository {

	AuthorizationRecord getAuthorization(String subject, String audience)
			throws InterruptedException, ExecutionException;
}
