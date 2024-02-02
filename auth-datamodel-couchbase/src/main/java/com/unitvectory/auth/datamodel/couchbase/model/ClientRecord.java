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
package com.unitvectory.auth.datamodel.couchbase.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.unitvectory.auth.datamodel.model.Client;
import com.unitvectory.auth.datamodel.model.ClientJwtBearer;
import com.unitvectory.auth.datamodel.model.ClientType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRecord implements Client {

	private String documentId;

	private String clientId;

	private String description;

	private String salt;

	private ClientType clientType;

	private String clientSecret1;

	private String clientSecret2;

	private List<ClientJwtBearerRecord> jwtBearer;

	@Override
	public List<ClientJwtBearer> getJwtBearer() {
		if (this.jwtBearer == null) {
			return Collections.emptyList();
		}

		return jwtBearer.stream().map(obj -> (ClientJwtBearer) obj)
				.collect(Collectors.toCollection(ArrayList::new));
	}
}
