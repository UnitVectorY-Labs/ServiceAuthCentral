package com.unitvectory.serviceauthcentral.model;

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
public class ClientRecord implements Client {

	@DocumentId
	private String documentId;

	private String clientId;

	private String description;

	private String salt;

	private String clientSecret1;

	private String clientSecret2;

	private List<JwtBearer> jwtBearer;

	public void setClientSecret1Plaintext(String clientSecret1) {
		if (clientSecret1 == null) {
			throw new IllegalArgumentException("clientSecret1 not set");
		}

		this.clientSecret1 = hashSecret(this.salt, clientSecret1);
	}

	public void setClientSecret2Plaintext(String clientSecret2) {
		if (clientSecret2 == null) {
			throw new IllegalArgumentException("clientSecret2 not set");
		}

		this.clientSecret2 = hashSecret(this.salt, clientSecret2);
	}

}