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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unitvectory.consistgen.epoch.EpochTimeProvider;
import com.unitvectory.serviceauthcentral.datamodel.memory.repository.MemoryAuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.memory.repository.MemoryClientRepository;
import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientScope;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientType;
import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.server.token.config.TestServiceAuthCentralConfig;
import com.unitvectory.serviceauthcentral.server.token.dto.TokenResponse;
import com.unitvectory.serviceauthcentral.server.token.model.JwtBuilder;
import com.unitvectory.serviceauthcentral.sign.service.SignService;

/**
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({ "test", "sign-local" })
@TestPropertySource(locations = "classpath:test-application.properties")
@Import(TestServiceAuthCentralConfig.class)
public class TokenResponseTest {

	@Value("${sac.issuer}")
	private String issuer;

	@Value("${sac.sign.local.active.kid}")
	private String kid;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AuthorizationRepository authorizationRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private SignService signService;

	@Autowired
	private EpochTimeProvider epochTimeProvider;

	@BeforeEach
	public void setUp() {

		// Populate Client Repository
		if (this.clientRepository instanceof MemoryClientRepository) {
			((MemoryClientRepository) this.clientRepository).reset();
		}

		this.clientRepository.putClient("bar", "Test2", "xyz", ClientType.APPLICATION,
				new ArrayList<ClientScope>());
		this.clientRepository.putClient("foo", "Test", "abc", ClientType.APPLICATION,
				new ArrayList<ClientScope>());
		Client client = this.clientRepository.getClient("foo");
		this.clientRepository.saveClientSecret1("foo", client.hashSecret("mySuperSecretfoo"));

		this.clientRepository.addAuthorizedJwt("foo", "myid", "http://example.com", issuer,
				"source", "foo");

		// Populate Authorization Repository
		if (this.authorizationRepository instanceof MemoryAuthorizationRepository) {
			((MemoryAuthorizationRepository) this.authorizationRepository).reset();
		}

		this.authorizationRepository.authorize("foo", "bar", new ArrayList<String>());

	}

	@SuppressWarnings("null")
	@Test
	public void postTokenInvalidGrantType() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "refresh_token");
		params.add("client_id", "foo");
		params.add("client_secret", "mySuperSecretfoo");
		params.add("audience", "bar");

		mockMvc.perform(
				post("/v1/token").params(params).contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().is(400));

	}

	@SuppressWarnings("null")
	@Test
	public void postTokenMissingAudience() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "client_credentials");
		params.add("client_id", "foo");
		params.add("client_secret", "mySuperSecretfoo");

		mockMvc.perform(
				post("/v1/token").params(params).contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().is(400));
	}

	@SuppressWarnings("null")
	@Test
	public void postTokenAuthorizationSuccessTest() throws Exception {

		JwtBuilder jwtBuilder = JwtBuilder.builder().withKeyId(kid).withIssuer(issuer)
				.withSubject("source").withAudience("foo");

		String kid = this.signService.getActiveKid(this.epochTimeProvider.epochTimeSeconds());
		String assertionToken = this.signService.sign(kid, jwtBuilder.buildUnsignedToken());

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
		params.add("client_id", "foo");
		params.add("assertion", assertionToken);
		params.add("audience", "bar");

		MvcResult mvcResult = mockMvc
				.perform(post("/v1/token").params(params)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().isOk())
				// access token (cannot validate the exact value as it is encoded and signed and
				// the exact value can change based on ordering of JSON attributes)
				.andExpect(jsonPath("$.access_token").exists())
				// token type
				.andExpect(jsonPath("$.token_type").value("Bearer"))
				// expires in
				.andExpect(jsonPath("$.expires_in").value(3600)).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		TokenResponse tokenResponse = objectMapper.readValue(responseBody, TokenResponse.class);

		// So instead we look for the key claims within the JWT
		DecodedJWT jwt = JWT.decode(tokenResponse.getAccess_token());
		assertEquals("myissuer", jwt.getIssuer());
		assertEquals("foo", jwt.getSubject());
		assertEquals("bar", jwt.getAudience().get(0));
	}

	@SuppressWarnings("null")
	@Test
	public void postTokenSecretSuccessTest() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "client_credentials");
		params.add("client_id", "foo");
		params.add("client_secret", "mySuperSecretfoo");
		params.add("audience", "bar");

		MvcResult mvcResult = mockMvc
				.perform(post("/v1/token").params(params)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().isOk())
				// access token (cannot validate the exact value as it is encoded and signed and
				// the exact value can change based on ordering of JSON attributes)
				.andExpect(jsonPath("$.access_token").exists())
				// token type
				.andExpect(jsonPath("$.token_type").value("Bearer"))
				// expires in
				.andExpect(jsonPath("$.expires_in").value(3600)).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		TokenResponse tokenResponse = objectMapper.readValue(responseBody, TokenResponse.class);

		// So instead we look for the key claims within the JWT
		DecodedJWT jwt = JWT.decode(tokenResponse.getAccess_token());
		assertEquals("myissuer", jwt.getIssuer());
		assertEquals("foo", jwt.getSubject());
		assertEquals("bar", jwt.getAudience().get(0));
	}

}
