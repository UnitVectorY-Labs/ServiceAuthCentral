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
package com.unitvectory.serviceauthcentral.server.token.model;

import lombok.Builder;
import lombok.Value;

/**
 * The User Context
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Value
@Builder
public class UserContext {

	/**
	 * The name of the provider for the user.
	 */
	private String provider;

	/**
	 * The globally unique immutable identifier for the user.
	 */
	private String userId;

	/**
	 * The display name for the user
	 */
	private String userName;

	/**
	 * Gets the userClientId
	 * 
	 * @return the userClientId
	 */
	public String getUserClientId() {
		return String.format("user:%s:%s", this.provider, this.userId);
	}
}
