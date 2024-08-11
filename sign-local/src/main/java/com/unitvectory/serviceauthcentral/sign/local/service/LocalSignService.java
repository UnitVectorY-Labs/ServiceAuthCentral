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
package com.unitvectory.serviceauthcentral.sign.local.service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import com.unitvectory.serviceauthcentral.sign.local.model.JsonWebKeyRecord;
import com.unitvectory.serviceauthcentral.sign.mapper.RsaPemToModulusExponentMapper;
import com.unitvectory.serviceauthcentral.sign.model.SignJwk;
import com.unitvectory.serviceauthcentral.sign.model.RsaMoulousExponent;
import com.unitvectory.serviceauthcentral.sign.service.SignService;
import com.unitvectory.serviceauthcentral.util.exception.InternalServerErrorException;

import lombok.AllArgsConstructor;

/**
 * The Sign Service for local signing.
 * 
 * This implementation depends on the private key to be accessable to the code base. While this
 * implementation is useful it is not the recommended one for the design philosophy behind
 * ServiceAuthCentral which is based on eliminating and locking down access to all secrets.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@AllArgsConstructor
public class LocalSignService implements SignService {

	private String activeKid;

	private String key1PrivateKey;

	private String key1PublicKey;

	private String key1Kid;

	@Override
	public String getActiveKid(long now) {
		return this.activeKid;
	}

	@Override
	public String sign(String kid, String unsignedToken) {
		String privateKeyPEM = null;
		if (activeKid.equals(key1Kid)) {
			privateKeyPEM = key1PrivateKey;
		} else {
			throw new InternalServerErrorException("no private key avaialble");
		}

		try {

			privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----", "")
					.replace("-----END PRIVATE KEY-----", "").replaceAll("\\s", "");
			byte[] pkcs8EncodedBytes = Base64.getDecoder().decode(privateKeyPEM);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
			PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

			// Sign the JWT
			Signature signature = Signature.getInstance("SHA256withRSA");
			signature.initSign(privateKey);
			signature.update(unsignedToken.getBytes());
			String signed = Base64.getUrlEncoder().withoutPadding().encodeToString(signature.sign())
					.split("=")[0];

			// Construct the JWT
			String jwt = unsignedToken + "." + signed;

			return jwt;
		} catch (Exception e) {
			throw new InternalServerErrorException("failed to sign", e);
		}
	}

	@Override
	public List<SignJwk> getAll() {

		List<SignJwk> list = new ArrayList<>();

		RsaMoulousExponent key1 =
				RsaPemToModulusExponentMapper.INSTANCE.convert(this.key1PublicKey);

		JsonWebKeyRecord jwksKey = JsonWebKeyRecord.builder().withKty("RSA")
				.withN(key1.getModulus()).withE(key1.getExponent()).withAlg("RS256")
				.withKid(this.key1Kid).withUse("sig").build();

		list.add(jwksKey);

		return Collections.unmodifiableList(list);
	}
}
