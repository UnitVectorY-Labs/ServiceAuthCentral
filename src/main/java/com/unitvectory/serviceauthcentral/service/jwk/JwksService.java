package com.unitvectory.serviceauthcentral.service.jwk;

import java.util.List;

import com.auth0.jwk.Jwk;

public interface JwksService {

	List<Jwk> getJwks(String url);

	Jwk getJwk(String url, String kid);

}
