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

import com.unitvectory.serviceauthcentral.datamodel.model.CachedJwk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

/**
 * The Cached JWK Record for DynamoDB
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class CachedJwkRecord implements CachedJwk {

	private String pk;

	private String url;

	private Long ttl;

	private boolean valid;

	private String kid;

	private String kty;

	private String alg;

	private String use;

	private String n;

	private String e;

	@DynamoDbPartitionKey
	@DynamoDbAttribute("pk")
	public String getPk() {
		return pk;
	}

	@DynamoDbSecondaryPartitionKey(indexNames = {"url-index"})
	@DynamoDbAttribute("url")
	public String getUrl() {
		return url;
	}

	@DynamoDbAttribute("ttl")
	public Long getTtl() {
		return ttl;
	}

	@Override
	@DynamoDbAttribute("valid")
	public boolean isValid() {
		return valid;
	}

	@Override
	@DynamoDbAttribute("kid")
	public String getKid() {
		return kid;
	}

	@Override
	@DynamoDbAttribute("kty")
	public String getKty() {
		return kty;
	}

	@Override
	@DynamoDbAttribute("alg")
	public String getAlg() {
		return alg;
	}

	@Override
	@DynamoDbAttribute("use")
	public String getUse() {
		return use;
	}

	@Override
	@DynamoDbAttribute("n")
	public String getN() {
		return n;
	}

	@Override
	@DynamoDbAttribute("e")
	public String getE() {
		return e;
	}

	@Override
	public boolean isExpired(long now) {
		if (ttl == null) {
			return true;
		}
		return ttl < now;
	}
}
