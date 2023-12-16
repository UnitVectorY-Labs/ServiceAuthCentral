package com.unitvectory.serviceauthcentral.repository.client;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.google.common.hash.Hashing;
import com.unitvectory.serviceauthcentral.model.ClientRecord;

public class MokedClientRepository implements ClientRepository {

	private static final Set<String> CLIENTS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("foo", "bar")));

	@Override
	public ClientRecord getClient(String clientId) throws InterruptedException, ExecutionException {
		if (clientId == null) {
			return null;
		}

		if (!CLIENTS.contains(clientId)) {
			return null;
		}

		ClientRecord.ClientRecordBuilder builder = ClientRecord.builder().clientId(clientId).documentId(clientId);

		ClientRecord clientRecord = builder.build();
		clientRecord.setSalt(Hashing.sha256().hashString(clientId, StandardCharsets.UTF_8).toString());

		clientRecord.setClientSecret1Plaintext("mySuperSecret" + clientId);
		return clientRecord;
	}

}
