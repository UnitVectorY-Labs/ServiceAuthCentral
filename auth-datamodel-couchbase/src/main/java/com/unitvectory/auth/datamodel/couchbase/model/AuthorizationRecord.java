package com.unitvectory.auth.datamodel.couchbase.model;

import com.unitvectory.auth.datamodel.model.Authorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationRecord implements Authorization {

	private String documentId;

	private String subject;

	private String audience;

}
