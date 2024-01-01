package com.unitvectory.auth.sign.local.service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import com.unitvectory.auth.sign.local.model.JsonWebKeyRecord;
import com.unitvectory.auth.sign.mapper.RsaPemToModulusExponentMapper;
import com.unitvectory.auth.sign.model.JsonWebKey;
import com.unitvectory.auth.sign.model.RsaMoulousExponent;
import com.unitvectory.auth.sign.service.SignService;
import com.unitvectory.auth.util.exception.InternalServerErrorException;

import lombok.AllArgsConstructor;

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
			String signed = Base64.getUrlEncoder().withoutPadding().encodeToString(signature.sign()).split("=")[0];

			// Construct the JWT
			String jwt = unsignedToken + "." + signed;

			return jwt;
		} catch (Exception e) {
			throw new InternalServerErrorException("failed to sign", e);
		}
	}

	@Override
	public List<JsonWebKey> getAll() {

		List<JsonWebKey> list = new ArrayList<>();

		RsaMoulousExponent key1 = RsaPemToModulusExponentMapper.INSTANCE.convert(this.key1PublicKey);

		JsonWebKeyRecord jwksKey = JsonWebKeyRecord.builder().withKty("RSA").withN(key1.getModulus())
				.withE(key1.getExponent()).withAlg("RS256").withKid(this.key1Kid).withUse("sig").build();

		list.add(jwksKey);

		return Collections.unmodifiableList(list);
	}
}
