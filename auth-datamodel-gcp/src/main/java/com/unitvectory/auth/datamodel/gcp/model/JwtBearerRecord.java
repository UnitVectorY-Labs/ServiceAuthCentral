package com.unitvectory.auth.datamodel.gcp.model;

import com.unitvectory.auth.datamodel.model.JwtBearer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtBearerRecord implements JwtBearer {

	private String jwksUrl;

	private String iss;

	private String sub;

	private String aud;

}
