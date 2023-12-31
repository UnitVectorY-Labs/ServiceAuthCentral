package com.unitvectory.auth.datamodel.gcp.model;

import com.unitvectory.auth.datamodel.model.ClientJwtBearer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtBearerRecord implements ClientJwtBearer {

	private String jwksUrl;

	private String iss;

	private String sub;

	private String aud;

}
