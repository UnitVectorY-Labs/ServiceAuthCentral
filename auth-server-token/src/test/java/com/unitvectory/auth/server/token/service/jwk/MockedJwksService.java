package com.unitvectory.auth.server.token.service.jwk;

import java.util.List;

import com.auth0.jwk.Jwk;

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
