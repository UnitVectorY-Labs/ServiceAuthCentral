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

/**
 * The JWKS Resolver that utilizies Auth0's client library.
 * 
 * This does not depend on the Auth0 service, just their library implementation.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
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
