/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.unitvectory.auth.datamodel.memory.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.unitvectory.auth.common.service.time.TimeService;
import com.unitvectory.auth.datamodel.memory.model.MemoryAuthorization;
import com.unitvectory.auth.datamodel.model.Authorization;
import com.unitvectory.auth.datamodel.repository.AuthorizationRepository;
import com.unitvectory.auth.util.exception.InternalServerErrorException;
import lombok.NonNull;

/**
 * The Memory Authorization Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public class MemoryAuthorizationRepository implements AuthorizationRepository {

	private List<MemoryAuthorization> authorizations;

	private TimeService timeService;

	public MemoryAuthorizationRepository(TimeService timeService) {
		this.authorizations = new ArrayList<>();
		this.timeService = timeService;
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
	public void deleteAuthorization(@NonNull String id) {
		MemoryAuthorization auth = (MemoryAuthorization) this.getAuthorization(id);
		if (auth != null) {
			this.authorizations.remove(auth);
		}
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
	public void authorize(@NonNull String subject, @NonNull String audience,
			@NonNull List<String> authorizedScopes) {

		for (MemoryAuthorization auth : this.authorizations) {
			if (auth.matches(subject, audience)) {
				// Already exists
				return;
			}
		}

		this.authorizations.add(MemoryAuthorization.builder()
				.authorizationCreated(this.timeService.getCurrentTimestamp()).subject(subject)
				.audience(audience).authorizedScopes(new ArrayList<>(authorizedScopes)).build());
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

	@Override
	public void authorizeAddScope(@NonNull String subject, @NonNull String audience,
			@NonNull String authorizedScope) {
		MemoryAuthorization a = null;
		for (MemoryAuthorization auth : this.authorizations) {
			if (auth.matches(subject, audience)) {
				a = auth;
				break;
			}
		}

		if (a == null) {
			throw new InternalServerErrorException("Authorization not found");
		}

		a.getAuthorizedScopes().add(authorizedScope);

	}

	@Override
	public void authorizeRemoveScope(@NonNull String subject, @NonNull String audience,
			@NonNull String authorizedScope) {
		MemoryAuthorization a = null;
		for (MemoryAuthorization auth : this.authorizations) {
			if (auth.matches(subject, audience)) {
				a = auth;
				break;
			}
		}

		if (a == null) {
			throw new InternalServerErrorException("Authorization not found");
		}

		a.getAuthorizedScopes().remove(authorizedScope);
	}
}
