package com.unitvectory.serviceauthcentral.model;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class JwtBearer {

	private String jwksUrl;

	private String iss;

	private String sub;

	private String aud;

	public boolean matches(DecodedJWT jwt) {
		if (jwt == null) {
			return false;
		}

		// Verify that 'iss', 'sub', and 'aud' are not null in this class
		if (this.iss == null || this.sub == null || this.aud == null) {
			return false;
		}

		// Check if 'iss', 'sub', and 'aud' match between this class and the decoded JWT
		boolean issMatch = this.iss.equals(jwt.getIssuer());
		boolean subMatch = this.sub.equals(jwt.getSubject());
		boolean audMatch = jwt.getAudience().contains(this.aud);

		return issMatch && subMatch && audMatch;
	}
}
