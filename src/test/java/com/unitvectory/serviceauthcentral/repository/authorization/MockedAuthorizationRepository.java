package com.unitvectory.serviceauthcentral.repository.authorization;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.unitvectory.serviceauthcentral.model.AuthorizationRecord;
import com.unitvectory.serviceauthcentral.repository.authorization.AuthorizationRepository;

public class MockedAuthorizationRepository implements AuthorizationRepository {

	private static final Map<String, Set<String>> AUTHORIZATIONS = getAuthorizations();

	private static Map<String, Set<String>> getAuthorizations() {
		Map<String, Set<String>> auth = new HashMap<>();

		Set<String> foo = new HashSet<>();
		foo.add("bar");
		auth.put("foo", Collections.unmodifiableSet(foo));

		return Collections.unmodifiableMap(auth);

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
