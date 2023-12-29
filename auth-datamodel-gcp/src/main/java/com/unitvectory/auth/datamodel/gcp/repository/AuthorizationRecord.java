package com.unitvectory.auth.datamodel.gcp.repository;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.IgnoreExtraProperties;
import com.unitvectory.auth.datamodel.model.Authorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IgnoreExtraProperties
class AuthorizationRecord implements Authorization {

	@DocumentId
	private String documentId;

	private String subject;

	private String audience;

}
