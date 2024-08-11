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
package com.unitvectory.auth.datamodel.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
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
