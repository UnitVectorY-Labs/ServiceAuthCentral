package com.unitvectory.serviceauthcentral.gcp.repository;

import com.unitvectory.serviceauthcentral.datamodel.model.JwtBearer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class JwtBearerRecord implements JwtBearer {

	private String jwksUrl;

	private String iss;

	private String sub;

	private String aud;

}
