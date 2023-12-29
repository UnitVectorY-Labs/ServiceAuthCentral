package com.unitvectory.serviceauthcentral.gcp.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.Exclude;
import com.google.cloud.firestore.annotation.IgnoreExtraProperties;
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
@IgnoreExtraProperties
class ClientRecord implements Client {

	@DocumentId
	private String documentId;

	private String clientId;

	private String description;

	private String salt;

	private String clientSecret1;

	private String clientSecret2;

	private List<JwtBearerRecord> jwtBearer;

	@Exclude
	@Override
	public List<JwtBearer> getJwtBearer() {
		if (this.jwtBearer == null) {
			return Collections.emptyList();
		}

		return jwtBearer.stream().map(obj -> (JwtBearer) obj).collect(Collectors.toCollection(ArrayList::new));
	}
}