package com.unitvectory.auth.datamodel.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ClientTest {

	private Client client;

	@BeforeEach
	public void setUp() {
		client = Mockito.mock(Client.class, Mockito.CALLS_REAL_METHODS);
	}

	@Test
	public void testHashSecretNull() {
		assertThrows(NullPointerException.class, () -> client.hashSecret(null),
				"Passing null secret should throw exception");
	}

	@Test
	public void testVerifySecretNull() {
		assertThrows(NullPointerException.class, () -> client.verifySecret(null),
				"Passing null secret should throw exception");
	}

	@Test
	public void testHashSecretWithNullSalt() {
		when(client.getSalt()).thenReturn(null);
		assertThrows(IllegalStateException.class, () -> client.hashSecret("secret"),
				"Expected hashSecret to throw, but it didn't");
	}

	@Test
	public void testHashSecret() {
		when(client.getSalt()).thenReturn("salt");
		assertNotNull(client.hashSecret("secret"), "Hashed secret should not be null");
	}

	@Test
	public void testVerifySecretWithNullSalt() {
		when(client.getSalt()).thenReturn(null);
		assertFalse(client.verifySecret("secret"), "Should return false if salt is null");
	}

	@Test
	public void testVerifySecretWithMatchingSecret1() {
		// Prepare the hash value outside of the stubbing to avoid any inline method
		// calls.
		String salt = "salt";
		when(client.getSalt()).thenReturn(salt);

		String secret = "secret";
		String hashedSecret = client.hashSecret(secret);

		when(client.getClientSecret1()).thenReturn(hashedSecret);

		assertTrue(client.verifySecret(secret),
				"Should return true if secret matches clientSecret1");
	}

	@Test
	public void testVerifySecretWithMatchingSecret2() {
		// Prepare the hash value outside of the stubbing to avoid any inline method
		// calls.
		String salt = "salt";
		when(client.getSalt()).thenReturn(salt);

		String secret = "secret2";
		String hashedSecret = client.hashSecret(secret);

		when(client.getClientSecret2()).thenReturn(hashedSecret);

		assertTrue(client.verifySecret(secret),
				"Should return true if secret matches clientSecret2");
	}

	@Test
	public void testVerifySecretWithNoSecretNoMatch() {
		when(client.getSalt()).thenReturn("salt");
		assertFalse(client.verifySecret("secret"), "Should return false if no secrets match");
	}

	@Test
	public void testVerifySecretWithNoMatch() {
		when(client.getSalt()).thenReturn("salt");
		when(client.getClientSecret1()).thenReturn("otherSecret1");
		when(client.getClientSecret2()).thenReturn("otherSecret2");
		assertFalse(client.verifySecret("secret"), "Should return false if no secrets match");
	}
}
