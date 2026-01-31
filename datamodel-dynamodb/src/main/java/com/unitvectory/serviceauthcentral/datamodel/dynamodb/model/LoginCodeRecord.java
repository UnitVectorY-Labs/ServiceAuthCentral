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

import com.unitvectory.serviceauthcentral.datamodel.model.LoginCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

/**
 * The Login Code Record for DynamoDB
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class LoginCodeRecord implements LoginCode {

	private String pk;

	private String clientId;

	private String redirectUri;

	private String codeChallenge;

	private String userClientId;

	private Long ttl;

	@DynamoDbPartitionKey
	@DynamoDbAttribute("pk")
	public String getPk() {
		return pk;
	}

	@Override
	@DynamoDbAttribute("clientId")
	public String getClientId() {
		return clientId;
	}

	@Override
	@DynamoDbAttribute("redirectUri")
	public String getRedirectUri() {
		return redirectUri;
	}

	@Override
	@DynamoDbAttribute("codeChallenge")
	public String getCodeChallenge() {
		return codeChallenge;
	}

	@Override
	@DynamoDbAttribute("userClientId")
	public String getUserClientId() {
		return userClientId;
	}

	@DynamoDbAttribute("ttl")
	public Long getTtl() {
		return ttl;
	}

	@Override
	public long getTimeToLive() {
		if (this.ttl != null) {
			return this.ttl;
		} else {
			return 0;
		}
	}
}
