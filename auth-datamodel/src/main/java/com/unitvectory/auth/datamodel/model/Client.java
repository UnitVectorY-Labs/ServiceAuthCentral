package com.unitvectory.auth.datamodel.model;

import java.util.List;

import com.unitvectory.auth.util.HashingUtil;

import lombok.NonNull;

public interface Client {

	String getDocumentId();

	String getClientId();

	String getDescription();

	String getSalt();

	String getClientSecret1();

	String getClientSecret2();

	List<JwtBearer> getJwtBearer();

	public default String hashSecret(@NonNull String secret) {
		if (this.getSalt() == null) {
			throw new IllegalStateException("salt not set");
		}

		String hashedSecret = HashingUtil.sha256(secret);
		return HashingUtil.sha256(hashedSecret + this.getSalt());
	}

	public default boolean verifySecret(@NonNull String secret) {

		if (this.getSalt() == null) {
			return false;
		}

		// Hash the secret so it can be compared
		String hashedSecret = this.hashSecret(secret);

		if (this.getClientSecret1() != null) {
			// Check to see if the secret matches 1
			if (this.getClientSecret1().equals(hashedSecret)) {
				return true;
			}
		}

		if (this.getClientSecret2() != null) {
			// Check to see if the secret matches 2
			if (this.getClientSecret2().equals(hashedSecret)) {
				return true;
			}
		}

		return false;
	}
}
