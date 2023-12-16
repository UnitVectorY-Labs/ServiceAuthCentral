package com.unitvectory.serviceauthcentral.service;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import com.unitvectory.serviceauthcentral.dto.JwksKey;
import com.unitvectory.serviceauthcentral.service.key.KeyService;

public class MokedKeyService implements KeyService {

	@Value("classpath:private_key.pem")
	private Resource privateKey;

	public String getActiveKeyName() {
		return "test-key";
	}

	public String signJwt(String keyName, String unsignedToken) {
		try {
			String privateKeyPEM = privateKey.getContentAsString(StandardCharsets.UTF_8);

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
			throw new RuntimeException(e);
		}
	}

	public List<JwksKey> getAllKeys() {
		List<JwksKey> keys = new ArrayList<>();
		keys.add(JwksKey.builder().withKty("RSA").withN(
				"tqfCGqvSde8iPoarVSqm_dAhn97JJ1s8DxBlmnrG7hI99g2PMn-KkuhbiYM07dU2RGsESsNPVSe7cWOpBz9AzMq5o_QY-sVgMXVCrZeJrJa6vg_cZ7N674JSXbLIVQCoWc9GPPk9NaJX5-K4kl89kthAUM40lqidum_Vrl5fw8UH7fv7-kkCdDcT94jpIrirRKNnc3KMpwAXIPCBUZTbx2F6luAYpjlxy6NPJLHkV8PRpEZYdBDUUwakEyatbycBQo4fosLoQczWA10s-gshPrQah14RzA3Oc0P-Rn244O-LwdV_7wwd8eBlL6XSCYjp7WcBsZRgW3yAFH9m_WR1jw")
				.withE("AQAB").withAlg("RS256")
				.withKid("5e78863ed1ffb9fc66b1d61634b126bf8eb20267e7996297eeeb9b19c8c0f732").withUse("sig")
				.withActive(true).withCreated(1698796800000l).withKeyName("my-key").build());
		return keys;
	}
}
