package com.unitvectory.auth.datamodel.model;

public interface JwtBearer {

	String getJwksUrl();

	String getIss();

	String getSub();

	String getAud();

}
