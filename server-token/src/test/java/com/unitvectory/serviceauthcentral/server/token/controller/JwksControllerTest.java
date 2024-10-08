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
package com.unitvectory.serviceauthcentral.server.token.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.unitvectory.serviceauthcentral.server.token.config.TestServiceAuthCentralConfig;

/**
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "sign-local"})
@TestPropertySource(locations = "classpath:test-application.properties")
@Import(TestServiceAuthCentralConfig.class)
public class JwksControllerTest {

	@Value("${sac.sign.local.key1.kid}")
	private String kid;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void postJwks() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.post("/.well-known/jwks.json"))
				.andExpect(MockMvcResultMatchers.status().is(405));
	}

	@SuppressWarnings("null")
	@Test
	public void getJwks() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/.well-known/jwks.json"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType("application/json"))
				// Asserting the structure of the JSON response
				// Checks if 'keys' is an array with one element
				.andExpect(jsonPath("$.keys", hasSize(1)))
				// kty
				.andExpect(jsonPath("$.keys[0].kty", is("RSA")))
				// n
				.andExpect(jsonPath("$.keys[0].n", is(
						"tqfCGqvSde8iPoarVSqm_dAhn97JJ1s8DxBlmnrG7hI99g2PMn-KkuhbiYM07dU2RGsESsNPVSe7cWOpBz9AzMq5o_QY-sVgMXVCrZeJrJa6vg_cZ7N674JSXbLIVQCoWc9GPPk9NaJX5-K4kl89kthAUM40lqidum_Vrl5fw8UH7fv7-kkCdDcT94jpIrirRKNnc3KMpwAXIPCBUZTbx2F6luAYpjlxy6NPJLHkV8PRpEZYdBDUUwakEyatbycBQo4fosLoQczWA10s-gshPrQah14RzA3Oc0P-Rn244O-LwdV_7wwd8eBlL6XSCYjp7WcBsZRgW3yAFH9m_WR1jw")))
				// e
				.andExpect(jsonPath("$.keys[0].e", is("AQAB")))
				// alg
				.andExpect(jsonPath("$.keys[0].alg", is("RS256")))
				// kid
				.andExpect(jsonPath("$.keys[0].kid", is(kid)))
				// use
				.andExpect(jsonPath("$.keys[0].use", is("sig")));
	}
}
