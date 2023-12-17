package com.unitvectory.serviceauthcentral.repository.jwk;

import java.util.List;

import com.auth0.jwk.Jwk;
import com.unitvectory.serviceauthcentral.service.jwk.JwksService;

public class MockedJwksService implements JwksService {

	@Override
	public List<Jwk> getJwks(String url) {
		return null;
	}

	@Override
	public Jwk getJwk(String url, String kid) {
		return null;
	}

}
