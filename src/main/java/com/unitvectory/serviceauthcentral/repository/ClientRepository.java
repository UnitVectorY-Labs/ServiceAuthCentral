package com.unitvectory.serviceauthcentral.repository;

import java.util.concurrent.ExecutionException;

import com.unitvectory.serviceauthcentral.model.AuthorizationRecord;
import com.unitvectory.serviceauthcentral.model.ClientRecord;

public interface ClientRepository {

	ClientRecord getClient(String clientId) throws InterruptedException, ExecutionException;

	AuthorizationRecord getAuthorization(String subject, String audience)
			throws InterruptedException, ExecutionException;
}
