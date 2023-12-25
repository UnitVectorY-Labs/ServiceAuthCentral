package com.unitvectory.serviceauthcentral.gcp.repository;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.cloud.firestore.annotation.DocumentId;
import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.datamodel.model.JwtBearer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
class ClientRecord implements Client {

	@DocumentId
	private String documentId;

	private String clientId;

	private String description;

	private String salt;

	private String clientSecret1;

	private String clientSecret2;

	private List<JwtBearer> jwtBearer;
}