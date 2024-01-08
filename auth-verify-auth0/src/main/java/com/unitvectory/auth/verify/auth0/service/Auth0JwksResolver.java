package com.unitvectory.auth.verify.auth0.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.UrlJwkProvider;
import com.unitvectory.auth.util.exception.InternalServerErrorException;
import com.unitvectory.auth.verify.auth0.mapper.JwkAuth0Mapper;
import com.unitvectory.auth.verify.model.VerifyJwk;
import com.unitvectory.auth.verify.model.VerifyJwks;
import com.unitvectory.auth.verify.service.JwksResolver;

public class Auth0JwksResolver implements JwksResolver {

	@Override
	public VerifyJwks getJwks(String url) {
		try {
			UrlJwkProvider provider = new UrlJwkProvider(new URL(url));

			List<VerifyJwk> list = new ArrayList<>();

			for (Jwk jwk : provider.getAll()) {

				list.add(JwkAuth0Mapper.INSTANCE.convert(jwk));
			}

			return VerifyJwks.builder().keys(Collections.unmodifiableList(list)).build();
		} catch (Exception e) {
			throw new InternalServerErrorException("failed to retrieve jwks", e);
		}
	}
}
