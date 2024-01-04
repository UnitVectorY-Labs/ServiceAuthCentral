package com.unitvectory.auth.datamodel.memory.model;

import com.unitvectory.auth.datamodel.model.ClientJwtBearer;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class MemoryClientJwtBearer implements ClientJwtBearer {

	private String id;

	private String jwksUrl;

	private String iss;

	private String sub;

	private String aud;
}
