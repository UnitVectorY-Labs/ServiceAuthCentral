package com.unitvectory.serviceauthcentral.service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.google.common.hash.Hashing;
import com.unitvectory.serviceauthcentral.model.AuthorizationRecord;
import com.unitvectory.serviceauthcentral.model.ClientRecord;
import com.unitvectory.serviceauthcentral.repository.ClientRepository;

public class MokedClientRepository implements ClientRepository {

	private static final Set<String> CLIENTS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("foo", "bar")));

	private static final Map<String, Set<String>> AUTHORIZATIONS = getAuthorizations();

	private static Map<String, Set<String>> getAuthorizations() {
		Map<String, Set<String>> auth = new HashMap<>();

		Set<String> foo = new HashSet<>();
		foo.add("bar");
		auth.put("foo", Collections.unmodifiableSet(foo));

		return Collections.unmodifiableMap(auth);

	}

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

		clientRecord.setClientSecret1Plaintext("mySecret_" + clientId);
		return clientRecord;
	}

	@Override
	public AuthorizationRecord getAuthorization(String subject, String audience)
			throws InterruptedException, ExecutionException {
		if (subject == null || audience == null) {
			return null;
		}

		if (!AUTHORIZATIONS.containsKey(subject) || !AUTHORIZATIONS.get(subject).contains(audience)) {
			return null;
		}

		return AuthorizationRecord.builder().subject(subject).audience(audience).documentId(subject + "|" + audience)
				.build();
	}

}
