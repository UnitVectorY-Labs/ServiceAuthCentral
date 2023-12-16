package com.unitvectory.serviceauthcentral.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.unitvectory.serviceauthcentral.config.TestServiceAuthCentralConfig;
import com.unitvectory.serviceauthcentral.dto.JwksKey;
import com.unitvectory.serviceauthcentral.service.entropy.EntropyService;
import com.unitvectory.serviceauthcentral.service.time.TimeService;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = { "serviceauthcentral.cache.jwks.hours=1", "serviceauthcentral.key.location=global",
		"serviceauthcentral.key.ring=authorization", "serviceauthcentral.key.name=jwk-key",
		"serviceauthcentral.jwt.issuer=myissuer", "google.cloud.project=test" })
@ActiveProfiles("test")
@Import(TestServiceAuthCentralConfig.class)
public class CryptoServiceTest {

	@Autowired
	private CryptoService cryptoService;

	@Autowired
	private TimeService timeService;

	@Autowired
	private EntropyService entropyService;

	@Value("classpath:public_key.pem")
	private Resource publicKey;

	@Test
	public void buildUnsignedJwtTest() {

		long validSeconds = 3600;
		String unsignedKey = this.cryptoService.buildUnsignedJwt("test-key", "my-subject", "my-audience", validSeconds);

		DecodedJWT jwt = JWT.decode(unsignedKey + ".a");

		assertEquals("RS256", jwt.getAlgorithm());
		assertEquals("62af8704764faf8ea82fc61ce9c4c3908b6cb97d463a634e9e587d7c885db0ef", jwt.getKeyId());
		assertEquals("JWT", jwt.getType());

		long now = this.timeService.getCurrentTimeSeconds();
		long expires = now + validSeconds;

		assertEquals(expires, jwt.getExpiresAtAsInstant().getEpochSecond());
		assertEquals(now, jwt.getIssuedAtAsInstant().getEpochSecond());
		assertEquals("myissuer", jwt.getIssuer());
		assertEquals(this.entropyService.generateUuid(), jwt.getId());
		assertEquals(now, jwt.getNotBeforeAsInstant().getEpochSecond());
		assertEquals("my-subject", jwt.getSubject());
		assertEquals("my-audience", jwt.getAudience().get(0));
	}

	@Test
	public void convertRsaPublicKeyTest() throws Exception {

		long now = this.timeService.getCurrentTimeSeconds();

		String pemKey = publicKey.getContentAsString(StandardCharsets.UTF_8);
		JwksKey jwksKey = cryptoService.convertRsaPublicKey("my-key", pemKey, "RS256", true, now);

		assertEquals("RSA", jwksKey.getKty());
		assertEquals("5e78863ed1ffb9fc66b1d61634b126bf8eb20267e7996297eeeb9b19c8c0f732", jwksKey.getKid());
		assertEquals("sig", jwksKey.getUse());
		assertEquals("RS256", jwksKey.getAlg());
		assertEquals(
				"tqfCGqvSde8iPoarVSqm_dAhn97JJ1s8DxBlmnrG7hI99g2PMn-KkuhbiYM07dU2RGsESsNPVSe7cWOpBz9AzMq5o_QY-sVgMXVCrZeJrJa6vg_cZ7N674JSXbLIVQCoWc9GPPk9NaJX5-K4kl89kthAUM40lqidum_Vrl5fw8UH7fv7-kkCdDcT94jpIrirRKNnc3KMpwAXIPCBUZTbx2F6luAYpjlxy6NPJLHkV8PRpEZYdBDUUwakEyatbycBQo4fosLoQczWA10s-gshPrQah14RzA3Oc0P-Rn244O-LwdV_7wwd8eBlL6XSCYjp7WcBsZRgW3yAFH9m_WR1jw",
				jwksKey.getN());
		assertEquals("AQAB", jwksKey.getE());
		assertEquals("my-key", jwksKey.getKeyName());
		assertEquals(true, jwksKey.isActive());
		assertEquals(now, jwksKey.getCreated());

	}
}
