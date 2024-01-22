package com.unitvectory.auth.server.token.model;

import lombok.Builder;
import lombok.Value;

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
