package com.unitvectory.serviceauthcentral.repository.client;

import java.util.concurrent.ExecutionException;

import com.unitvectory.serviceauthcentral.model.ClientRecord;

public interface ClientRepository {

	ClientRecord getClient(String clientId) throws InterruptedException, ExecutionException;

}
