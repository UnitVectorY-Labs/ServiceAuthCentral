package com.unitvectory.serviceauthcentral.gcp.repository;

import com.google.cloud.firestore.annotation.DocumentId;
import com.unitvectory.serviceauthcentral.datamodel.model.Authorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class AuthorizationRecord implements Authorization {

	@DocumentId
	private String documentId;

	private String subject;

	private String audience;

}
