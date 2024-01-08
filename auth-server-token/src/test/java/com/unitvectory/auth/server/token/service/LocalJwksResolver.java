package com.unitvectory.auth.server.token.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.unitvectory.auth.server.token.mapper.JwkMapper;
import com.unitvectory.auth.sign.model.SignJwk;
import com.unitvectory.auth.sign.service.SignService;
import com.unitvectory.auth.verify.model.VerifyJwk;
import com.unitvectory.auth.verify.model.VerifyJwks;
import com.unitvectory.auth.verify.service.JwksResolver;

public class LocalJwksResolver implements JwksResolver {

	@Autowired
	private SignService signService;

	@Override
	public VerifyJwks getJwks(String url) {

		// This is just for testing, the only thing this can return is the public key
		// that was used for signing

		List<SignJwk> jwks = this.signService.getAll();

		VerifyJwks.VerifyJwksBuilder builder = VerifyJwks.builder();

		List<VerifyJwk> keys = new ArrayList<>();
		for (SignJwk jwk : jwks) {
			keys.add(JwkMapper.INSTANCE.signJwkToVerifyJwk(jwk));
		}

		builder.keys(Collections.unmodifiableList(keys));

		return builder.build();
	}

}
