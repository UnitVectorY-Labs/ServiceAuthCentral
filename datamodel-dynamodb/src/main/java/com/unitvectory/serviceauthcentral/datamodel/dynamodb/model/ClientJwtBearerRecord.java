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

import com.unitvectory.serviceauthcentral.datamodel.model.ClientJwtBearer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

/**
 * The Client JWT Bearer Record for DynamoDB
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class ClientJwtBearerRecord implements ClientJwtBearer {

	private String id;

	private String jwksUrl;

	private String iss;

	private String sub;

	private String aud;

	@Override
	@DynamoDbAttribute("id")
	public String getId() {
		return id;
	}

	@Override
	@DynamoDbAttribute("jwksUrl")
	public String getJwksUrl() {
		return jwksUrl;
	}

	@Override
	@DynamoDbAttribute("iss")
	public String getIss() {
		return iss;
	}

	@Override
	@DynamoDbAttribute("sub")
	public String getSub() {
		return sub;
	}

	@Override
	@DynamoDbAttribute("aud")
	public String getAud() {
		return aud;
	}
}
