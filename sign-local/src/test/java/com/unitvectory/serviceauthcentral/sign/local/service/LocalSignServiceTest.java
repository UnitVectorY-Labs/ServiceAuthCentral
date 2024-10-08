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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.unitvectory.serviceauthcentral.sign.local.config.SignLocalConfig;
import com.unitvectory.serviceauthcentral.sign.model.SignJwk;
import com.unitvectory.serviceauthcentral.sign.service.SignService;

/**
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "sign-local"})
@TestPropertySource(locations = "classpath:application-test.properties")
@ContextConfiguration(classes = SignLocalConfig.class)
public class LocalSignServiceTest {

	@Autowired
	private SignService signService;

	@Test
	public void testActiveKid() {
		String activeKid = this.signService.getActiveKid(0);
		assertEquals("foo", activeKid);
	}

	@Test
	public void testSign() {
		String signed = this.signService.sign("foo", "test.key");
		assertEquals(
				"test.key.IA2jVzxgWdPueIoiNUqCIxYzirh6XGrmPaf1463ctqkoGi2vZ0l8-15KVAmd6i0Y1Y_UuM12VAMjLdlGx3AvWruZjdOTgseuV-QirptERSyD93uXj1CkuqWR0Ff1uvQkJ25lhbrQnDXs5jFJMZlyLrIynO8OXhCbja7-HviiegpuXdh2arMkbKLYUVVbRTYDkl0k5dq29oRPC523YWSEh7k1yU27CHLxTT65ZVUH4fQAjrFdjIlFXjICTrUH6xp5w4oMZEUitoRrkaSjugutuC0jlGziywmxDKhGNixw8HaTkmA5XZdIp6FVYAE_rBbrCM0RLkrMqH8PSVThZfkskA",
				signed);
	}

	@Test
	public void testGetAll() {
		List<SignJwk> keys = this.signService.getAll();

		assertEquals(1, keys.size());

		SignJwk key = keys.get(0);

		assertEquals("RSA", key.getKty());
		assertEquals("foo", key.getKid());
		assertEquals("sig", key.getUse());
		assertEquals("RS256", key.getAlg());
		assertEquals(
				"tqfCGqvSde8iPoarVSqm_dAhn97JJ1s8DxBlmnrG7hI99g2PMn-KkuhbiYM07dU2RGsESsNPVSe7cWOpBz9AzMq5o_QY-sVgMXVCrZeJrJa6vg_cZ7N674JSXbLIVQCoWc9GPPk9NaJX5-K4kl89kthAUM40lqidum_Vrl5fw8UH7fv7-kkCdDcT94jpIrirRKNnc3KMpwAXIPCBUZTbx2F6luAYpjlxy6NPJLHkV8PRpEZYdBDUUwakEyatbycBQo4fosLoQczWA10s-gshPrQah14RzA3Oc0P-Rn244O-LwdV_7wwd8eBlL6XSCYjp7WcBsZRgW3yAFH9m_WR1jw",
				key.getN());
		assertEquals("AQAB", key.getE());
	}
}
