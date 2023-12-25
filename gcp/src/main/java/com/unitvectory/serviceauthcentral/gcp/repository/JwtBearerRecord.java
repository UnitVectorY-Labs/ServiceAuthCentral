package com.unitvectory.serviceauthcentral.gcp.repository;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
class JwtBearerRecord implements JwtBearer {

	private String jwksUrl;

	private String iss;

	private String sub;

	private String aud;

}
