package com.unitvectory.serviceauthcentral.gcp.repository;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
@JsonInclude(Include.NON_NULL)
class AuthorizationRecord implements Authorization {

	@DocumentId
	private String documentId;

	private String subject;

	private String audience;

}
