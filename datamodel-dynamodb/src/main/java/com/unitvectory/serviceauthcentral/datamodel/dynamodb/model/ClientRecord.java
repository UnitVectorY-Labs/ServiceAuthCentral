/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.unitvectory.serviceauthcentral.datamodel.dynamodb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientJwtBearer;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientScope;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

/**
 * The Client Record for DynamoDB
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class ClientRecord implements Client {

	private String pk;

	private String clientCreated;

	private String clientId;

	private String description;

	private String salt;

	private ClientType clientType;

	private String clientSecret1;

	private String clientSecret1Updated;

	private String clientSecret2;

	private String clientSecret2Updated;

	private List<ClientScopeRecord> availableScopes;

	private Boolean locked;

	private List<ClientJwtBearerRecord> jwtBearer;

	@DynamoDbPartitionKey
	@DynamoDbAttribute("pk")
	public String getPk() {
		return pk;
	}

	@Override
	@DynamoDbAttribute("clientCreated")
	public String getClientCreated() {
		return clientCreated;
	}

	@Override
	@DynamoDbAttribute("clientId")
	public String getClientId() {
		return clientId;
	}

	@Override
	@DynamoDbAttribute("description")
	public String getDescription() {
		return description;
	}

	@Override
	@DynamoDbAttribute("salt")
	public String getSalt() {
		return salt;
	}

	@DynamoDbAttribute("clientType")
	public ClientType getClientType() {
		return clientType;
	}

	@Override
	@DynamoDbAttribute("clientSecret1")
	public String getClientSecret1() {
		return clientSecret1;
	}

	@Override
	@DynamoDbAttribute("clientSecret1Updated")
	public String getClientSecret1Updated() {
		return clientSecret1Updated;
	}

	@Override
	@DynamoDbAttribute("clientSecret2")
	public String getClientSecret2() {
		return clientSecret2;
	}

	@Override
	@DynamoDbAttribute("clientSecret2Updated")
	public String getClientSecret2Updated() {
		return clientSecret2Updated;
	}

	@DynamoDbAttribute("availableScopes")
	public List<ClientScopeRecord> getAvailableScopesRecord() {
		return availableScopes;
	}

	public void setAvailableScopesRecord(List<ClientScopeRecord> availableScopes) {
		this.availableScopes = availableScopes;
	}

	@Override
	@DynamoDbIgnore
	public List<ClientScope> getAvailableScopes() {
		if (this.availableScopes == null) {
			return Collections.emptyList();
		}

		return availableScopes.stream().map(obj -> (ClientScope) obj)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	@DynamoDbAttribute("locked")
	public Boolean getLocked() {
		return locked;
	}

	@DynamoDbAttribute("jwtBearer")
	public List<ClientJwtBearerRecord> getJwtBearerRecord() {
		return jwtBearer;
	}

	public void setJwtBearerRecord(List<ClientJwtBearerRecord> jwtBearer) {
		this.jwtBearer = jwtBearer;
	}

	@Override
	@DynamoDbIgnore
	public List<ClientJwtBearer> getJwtBearer() {
		if (this.jwtBearer == null) {
			return Collections.emptyList();
		}

		return jwtBearer.stream().map(obj -> (ClientJwtBearer) obj)
				.collect(Collectors.toCollection(ArrayList::new));
	}
}
