package com.unitvectory.serviceauthcentral.service.jwk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.SigningKeyNotFoundException;
import com.auth0.jwk.UrlJwkProvider;
import com.unitvectory.serviceauthcentral.exception.InternalServerErrorException;

public class RemoteJwksServiceTest {

	@Mock
	private UrlJwkProvider mockProvider;

	@InjectMocks
	private RemoteJwksService service;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	/// @Test
	void testGetJwksSuccess() throws Exception {
		String url = "https://example.com";
		List<Jwk> expectedJwks = Arrays.asList(mock(Jwk.class), mock(Jwk.class));
		when(mockProvider.getAll()).thenReturn(expectedJwks);

		List<Jwk> result = service.getJwks(url);

		assertEquals(expectedJwks, result);
		verify(mockProvider).getAll();
	}

	@Test
	void testGetJwksThrowsException() throws SigningKeyNotFoundException {
		String url = "invalid-url";
		when(mockProvider.getAll()).thenThrow(new RuntimeException("Test exception"));

		assertThrows(InternalServerErrorException.class, () -> service.getJwks(url));
	}

	// @Test
	void testGetJwkSuccess() throws Exception {
		String url = "https://example.com";
		String kid = "test-kid";
		Jwk expectedJwk = mock(Jwk.class);
		when(mockProvider.get(kid)).thenReturn(expectedJwk);

		Jwk result = service.getJwk(url, kid);

		assertEquals(expectedJwk, result);
		verify(mockProvider).get(kid);
	}

	@Test
	void testGetJwkThrowsException() throws JwkException {
		String url = "invalid-url";
		String kid = "test-kid";
		when(mockProvider.get(kid)).thenThrow(new RuntimeException("Test exception"));

		assertThrows(InternalServerErrorException.class, () -> service.getJwk(url, kid));
	}
}
