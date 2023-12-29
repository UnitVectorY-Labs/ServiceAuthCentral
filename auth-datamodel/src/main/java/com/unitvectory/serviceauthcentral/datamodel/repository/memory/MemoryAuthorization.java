package com.unitvectory.serviceauthcentral.datamodel.repository.memory;

import com.unitvectory.serviceauthcentral.datamodel.model.Authorization;
import com.unitvectory.serviceauthcentral.datamodel.util.HashingUtil;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
class MemoryAuthorization implements Authorization {

	private String subject;

	private String audience;

	public boolean matches(@NonNull String subject, @NonNull String audience) {
		return subject.equals(this.subject) && audience.equals(this.audience);
	}

	@Override
	public String getDocumentId() {
		String subjectHash = HashingUtil.sha256(subject);
		String audienceHash = HashingUtil.sha256(audience);
		return HashingUtil.sha256(subjectHash + audienceHash);
	}
}
