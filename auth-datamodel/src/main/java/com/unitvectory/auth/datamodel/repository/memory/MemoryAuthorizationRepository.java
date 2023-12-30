package com.unitvectory.auth.datamodel.repository.memory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.unitvectory.auth.datamodel.model.Authorization;
import com.unitvectory.auth.datamodel.model.memory.MemoryAuthorization;
import com.unitvectory.auth.datamodel.repository.AuthorizationRepository;

import lombok.NonNull;

public class MemoryAuthorizationRepository implements AuthorizationRepository {

	private List<MemoryAuthorization> authorizations;

	public MemoryAuthorizationRepository() {
		this.authorizations = new ArrayList<>();
	}

	public void reset() {
		this.authorizations.clear();
	}

	@Override
	public Authorization getAuthorization(@NonNull String id) {
		for (MemoryAuthorization auth : this.authorizations) {
			if (id.equals(auth.getDocumentId())) {
				return auth;
			}
		}
		return null;
	}

	@Override
	public Authorization getAuthorization(@NonNull String subject, @NonNull String audience) {
		for (MemoryAuthorization auth : this.authorizations) {
			if (auth.matches(subject, audience)) {
				return auth;
			}
		}
		return null;
	}

	@Override
	public Iterator<Authorization> getAuthorizationBySubject(@NonNull String subject) {
		ArrayList<Authorization> list = new ArrayList<Authorization>();

		for (MemoryAuthorization auth : this.authorizations) {
			if (subject.equals(auth.getSubject())) {
				list.add(auth);
			}
		}

		return list.iterator();
	}

	@Override
	public Iterator<Authorization> getAuthorizationByAudience(@NonNull String audience) {
		ArrayList<Authorization> list = new ArrayList<Authorization>();

		for (MemoryAuthorization auth : this.authorizations) {
			if (audience.equals(auth.getAudience())) {
				list.add(auth);
			}
		}

		return list.iterator();
	}

	@Override
	public void authorize(@NonNull String subject, @NonNull String audience) {

		for (MemoryAuthorization auth : this.authorizations) {
			if (auth.matches(subject, audience)) {
				// Already exists
				return;
			}
		}

		this.authorizations.add(MemoryAuthorization.builder().subject(subject).audience(audience).build());
	}

	@Override
	public void deauthorize(@NonNull String subject, @NonNull String audience) {
		MemoryAuthorization match = null;
		for (MemoryAuthorization auth : this.authorizations) {
			if (auth.matches(subject, audience)) {
				match = auth;
				break;
			}
		}

		if (match != null) {
			this.authorizations.remove(match);
		}
	}
}
