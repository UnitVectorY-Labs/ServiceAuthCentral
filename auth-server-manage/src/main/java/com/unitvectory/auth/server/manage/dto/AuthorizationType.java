package com.unitvectory.auth.server.manage.dto;

import com.unitvectory.auth.datamodel.model.Authorization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationType {

	private String id;

	private String subjectId;

	private String audienceId;

	public AuthorizationType(Authorization auth) {
		this.id = auth.getDocumentId();
		this.subjectId = auth.getSubject();
		this.audienceId = auth.getAudience();
	}
}
