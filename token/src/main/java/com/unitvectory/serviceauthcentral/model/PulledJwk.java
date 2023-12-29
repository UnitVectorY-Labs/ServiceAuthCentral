package com.unitvectory.serviceauthcentral.model;

import com.auth0.jwk.Jwk;
import com.unitvectory.serviceauthcentral.datamodel.model.CachedJwk;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PulledJwk implements CachedJwk {

	private final Jwk jwk;

	@Override
	public boolean isValid() {
		return jwk != null;
	}

	@Override
	public boolean isExpired(long now) {
		return false;
	}

	@Override
	public String getKid() {
		return jwk.getId();
	}

	@Override
	public String getKty() {
		return jwk.getType();
	}

	@Override
	public String getAlg() {
		return jwk.getAlgorithm();
	}

	@Override
	public String getUse() {
		return jwk.getUsage();
	}

	@Override
	public String getN() {
		return (String) jwk.getAdditionalAttributes().get("n");
	}

	@Override
	public String getE() {
		return (String) jwk.getAdditionalAttributes().get("e");
	}

}
