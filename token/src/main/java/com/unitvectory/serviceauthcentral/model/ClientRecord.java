package com.unitvectory.serviceauthcentral.model;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.common.hash.Hashing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ClientRecord {

	@DocumentId
	private String documentId;

	private String clientId;

	private String salt;

	private String clientSecret1;

	private String clientSecret2;

	private List<JwtBearer> jwtBearer;

	public boolean verifySecret(String secret) {
		if (secret == null) {
			return false;
		}

		if (this.salt == null) {
			return false;
		}

		// Hash the secret so it can be compared
		String hashedSecret = this.hashSecret(this.salt, secret);

		if (this.clientSecret1 != null) {
			// Check to see if the secret matches 1
			if (this.clientSecret1.equals(hashedSecret)) {
				return true;
			}
		}

		if (this.clientSecret2 != null) {
			// Check to see if the secret matches 2
			if (this.clientSecret2.equals(hashedSecret)) {
				return true;
			}
		}

		return false;
	}

	public void setClientSecret1Plaintext(String clientSecret1) {
		if (clientSecret1 == null) {
			throw new IllegalArgumentException("clientSecret1 not set");
		}

		this.clientSecret1 = hashSecret(this.salt, clientSecret1);
	}

	public void setClientSecret2Plaintext(String clientSecret2) {
		if (clientSecret2 == null) {
			throw new IllegalArgumentException("clientSecret2 not set");
		}

		this.clientSecret2 = hashSecret(this.salt, clientSecret2);
	}

	private String hashSecret(String salt, String secret) {
		if (salt == null) {
			throw new IllegalStateException("salt not set");
		}

		String hashedSecret = Hashing.sha256().hashString(secret, StandardCharsets.UTF_8).toString();
		return Hashing.sha256().hashString(hashedSecret + salt, StandardCharsets.UTF_8).toString();
	}

}