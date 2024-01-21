package com.unitvectory.auth.datamodel.memory.model;

import java.util.List;

import com.unitvectory.auth.datamodel.model.Client;
import com.unitvectory.auth.datamodel.model.ClientJwtBearer;
import com.unitvectory.auth.datamodel.model.ClientType;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class MemoryClient implements Client {

	private String clientId;

	private String description;

	private String salt;

	private ClientType clientType;

	private String clientSecret1;

	private String clientSecret2;

	private List<ClientJwtBearer> jwtBearer;

}
