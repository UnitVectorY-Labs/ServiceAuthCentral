package com.unitvectory.serviceauthcentral.service.jwk;

import java.net.URL;
import java.util.List;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.UrlJwkProvider;
import com.unitvectory.serviceauthcentral.datamodel.exception.InternalServerErrorException;

public class RemoteJwksService implements JwksService {

	@Override
	public List<Jwk> getJwks(String url) {
		try {
			UrlJwkProvider provider = new UrlJwkProvider(new URL(url));
			return provider.getAll();
		} catch (Exception e) {
			throw new InternalServerErrorException("failed to retrieve jwks", e);
		}
	}

	@Override
	public Jwk getJwk(String url, String kid) {
		try {
			UrlJwkProvider provider = new UrlJwkProvider(new URL(url));
			return provider.get(kid);
		} catch (Exception e) {
			throw new InternalServerErrorException("failed to retrieve jwks", e);
		}
	}
}
