package com.unitvectory.serviceauthcentral.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.unitvectory.serviceauthcentral.config.TestServiceAuthCentralConfig;
import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.memory.MemoryAuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.memory.MemoryClientRepository;
import com.unitvectory.serviceauthcentral.dto.TokenResponse;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:test-application.properties")
@Import(TestServiceAuthCentralConfig.class)
public class TokenResponseTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AuthorizationRepository authorizationRepository;

	@Autowired
	private ClientRepository clientRepository;

	@BeforeEach
	public void setUp() {

		if (this.clientRepository instanceof MemoryClientRepository) {
			((MemoryClientRepository) this.clientRepository).reset();
		}

		this.clientRepository.putClient("bar", "Test2", "xyz");
		this.clientRepository.putClient("foo", "Test", "abc");
		Client client = this.clientRepository.getClient("foo");
		this.clientRepository.saveClientSecret1("foo", client.hashSecret("mySuperSecretfoo"));

		if (this.authorizationRepository instanceof MemoryAuthorizationRepository) {
			((MemoryAuthorizationRepository) this.authorizationRepository).reset();
		}

		this.authorizationRepository.authorize("foo", "bar");

	}

	@Test
	public void postTokenInvalidGrantType() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "refresh_token");
		params.add("client_id", "foo");
		params.add("client_secret", "mySuperSecretfoo");
		params.add("audience", "bar");

		mockMvc.perform(post("/v1/token").params(params).contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().is(400));

	}

	@Test
	public void postTokenMissingAudience() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "client_credentials");
		params.add("client_id", "foo");
		params.add("client_secret", "mySuperSecretfoo");

		mockMvc.perform(post("/v1/token").params(params).contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().is(400));
	}

	@Test
	public void postTokenSuccessTest() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "client_credentials");
		params.add("client_id", "foo");
		params.add("client_secret", "mySuperSecretfoo");
		params.add("audience", "bar");

		MvcResult mvcResult = mockMvc
				.perform(post("/v1/token").params(params).contentType(MediaType.APPLICATION_FORM_URLENCODED))
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
