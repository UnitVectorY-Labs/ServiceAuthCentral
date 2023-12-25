package com.unitvectory.serviceauthcentral.datamodel.repository;

import com.unitvectory.serviceauthcentral.datamodel.model.Authorization;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class MemoryAuthorization implements Authorization {

	private String subject;

	private String audience;

	public boolean matches(@NonNull String subject, @NonNull String audience) {
		return subject.equals(this.subject) && audience.equals(this.audience);
	}

}
