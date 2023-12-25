package com.unitvectory.serviceauthcentral.gcp.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
class ClientRecord implements Client {

	@DocumentId
	private String documentId;

	private String clientId;

	private String description;

	private String salt;

	private String clientSecret1;

	private String clientSecret2;

	private List<JwtBearerRecord> jwtBearerRecord;

	@Override
	public List<JwtBearer> getJwtBearer() {
		return jwtBearerRecord.stream().map(obj -> (JwtBearer) obj).collect(Collectors.toCollection(ArrayList::new));
	}

}