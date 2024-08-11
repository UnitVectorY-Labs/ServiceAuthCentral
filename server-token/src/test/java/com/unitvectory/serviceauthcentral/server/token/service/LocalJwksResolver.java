/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.unitvectory.serviceauthcentral.server.token.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.unitvectory.serviceauthcentral.server.token.mapper.JwkMapper;
import com.unitvectory.serviceauthcentral.sign.model.SignJwk;
import com.unitvectory.serviceauthcentral.sign.service.SignService;
import com.unitvectory.serviceauthcentral.verify.model.VerifyJwk;
import com.unitvectory.serviceauthcentral.verify.model.VerifyJwks;
import com.unitvectory.serviceauthcentral.verify.service.JwksResolver;

/**
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
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
