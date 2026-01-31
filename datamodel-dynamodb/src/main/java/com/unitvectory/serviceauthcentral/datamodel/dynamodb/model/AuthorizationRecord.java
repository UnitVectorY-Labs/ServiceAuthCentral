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
import java.util.List;

import com.unitvectory.serviceauthcentral.datamodel.model.Authorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

/**
 * The Authorization Record for DynamoDB
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class AuthorizationRecord implements Authorization {

	private String pk;

	private String authorizationCreated;

	private String subject;

	private String audience;

	private Boolean locked;

	@Builder.Default
	private List<String> authorizedScopes = new ArrayList<>();

	@DynamoDbPartitionKey
	@DynamoDbAttribute("pk")
	public String getPk() {
		return pk;
	}

	@Override
	public String getDocumentId() {
		return pk;
	}

	@Override
	@DynamoDbAttribute("authorizationCreated")
	public String getAuthorizationCreated() {
		return authorizationCreated;
	}

	@Override
	@DynamoDbSecondaryPartitionKey(indexNames = {"subject-index"})
	@DynamoDbAttribute("subject")
	public String getSubject() {
		return subject;
	}

	@Override
	@DynamoDbSecondaryPartitionKey(indexNames = {"audience-index"})
	@DynamoDbAttribute("audience")
	public String getAudience() {
		return audience;
	}

	@DynamoDbAttribute("locked")
	public Boolean getLocked() {
		return locked;
	}

	@Override
	@DynamoDbAttribute("authorizedScopes")
	public List<String> getAuthorizedScopes() {
		return authorizedScopes;
	}
}
