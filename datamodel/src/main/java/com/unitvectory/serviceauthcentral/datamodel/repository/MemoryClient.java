package com.unitvectory.serviceauthcentral.datamodel.repository;

import java.util.List;

import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.datamodel.model.JwtBearer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
class MemoryClient implements Client {

	private String clientId;

	private String description;

	private String salt;

	private String clientSecret1;

	private String clientSecret2;

	private List<JwtBearer> jwtBearer;

	@Override
	public String getDocumentId() {
		return this.clientId;
	}

}
