package com.unitvectory.auth.datamodel.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ClientJwtBearerTest {

	private ClientJwtBearer jwtA;
	private ClientJwtBearer jwtB;

	@BeforeEach
	public void setUp() {
		jwtA = Mockito.mock(ClientJwtBearer.class, Mockito.CALLS_REAL_METHODS);
		jwtB = Mockito.mock(ClientJwtBearer.class, Mockito.CALLS_REAL_METHODS);
	}

	@Test
	public void verifyMatchesSameObject() {
		assertTrue(jwtA.matches(jwtA));
	}

	@Test
	public void verifyMatchesNullObject() {
		assertFalse(jwtA.matches(null));
	}

	@Test
    public void verifyMatchesDifferentObjects() {
        when(jwtA.getJwksUrl()).thenReturn("urlA");
        when(jwtA.getIss()).thenReturn("issA");
        when(jwtA.getSub()).thenReturn("subA");
        when(jwtA.getAud()).thenReturn("audA");

        when(jwtB.getJwksUrl()).thenReturn("urlA");
        when(jwtB.getIss()).thenReturn("issA");
        when(jwtB.getSub()).thenReturn("subA");
        when(jwtB.getAud()).thenReturn("audA");

        assertTrue(jwtA.matches(jwtB));
    }

	@Test
    public void verifyMismatchJwksUrl() {
        when(jwtA.getJwksUrl()).thenReturn("urlA");
        when(jwtB.getJwksUrl()).thenReturn("urlB");

        when(jwtA.getIss()).thenReturn("iss");
        when(jwtB.getIss()).thenReturn("iss");

        when(jwtA.getSub()).thenReturn("sub");
        when(jwtB.getSub()).thenReturn("sub");

        when(jwtA.getAud()).thenReturn("aud");
        when(jwtB.getAud()).thenReturn("aud");

        assertFalse(jwtA.matches(jwtB));
    }

	@Test
    public void verifyMismatchIss() {
        when(jwtA.getIss()).thenReturn("issA");
        when(jwtB.getIss()).thenReturn("issB");

        assertFalse(jwtA.matches(jwtB));
    }

	@Test
    public void verifyMismatchSub() {
        when(jwtA.getSub()).thenReturn("subA");
        when(jwtB.getSub()).thenReturn("subB");

        assertFalse(jwtA.matches(jwtB));
    }

	@Test
    public void verifyMismatchAud() {
        when(jwtA.getAud()).thenReturn("audA");
        when(jwtB.getAud()).thenReturn("audB");

        assertFalse(jwtA.matches(jwtB));
    }
}
