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
package com.unitvectory.serviceauthcentral.datamodel.postgres.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.unitvectory.serviceauthcentral.datamodel.model.Authorization;
import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientScope;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientSummaryConnection;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientType;
import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;

/**
 * Integration tests for PostgreSQL repository implementations
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@SpringBootTest(classes = TestConfig.class)
@ActiveProfiles("test")
@Transactional
public class PostgresRepositoryIntegrationTest {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AuthorizationRepository authorizationRepository;

    // Note: @Transactional annotation on the test class ensures that all database
    // operations are rolled back after each test, providing automatic cleanup.

    @Test
    void testClientCrud() {
        String clientId = "test-client-" + System.currentTimeMillis();
        String description = "Test Client Description";
        String salt = "test-salt-12345";
        List<ClientScope> scopes = new ArrayList<>();

        // Create client
        clientRepository.putClient(clientId, description, salt, ClientType.APPLICATION, scopes);

        // Read client
        Client client = clientRepository.getClient(clientId);
        assertNotNull(client);
        assertEquals(clientId, client.getClientId());
        assertEquals(description, client.getDescription());
        assertEquals(ClientType.APPLICATION, client.getClientType());

        // List clients
        ClientSummaryConnection connection = clientRepository.getClients(10, null, null, null);
        assertNotNull(connection);
        assertNotNull(connection.getEdges());
        assertTrue(connection.getEdges().size() > 0);

        // Delete client
        clientRepository.deleteClient(clientId);
        Client deletedClient = clientRepository.getClient(clientId);
        assertNull(deletedClient);
    }

    @Test
    void testClientSecrets() {
        String clientId = "secret-test-client-" + System.currentTimeMillis();
        List<ClientScope> scopes = new ArrayList<>();

        // Create client
        clientRepository.putClient(clientId, "Description", "salt", ClientType.APPLICATION, scopes);

        // Set and verify secret1
        String hashedSecret1 = "hashed-secret-1";
        clientRepository.saveClientSecret1(clientId, hashedSecret1);
        Client client = clientRepository.getClient(clientId);
        assertEquals(hashedSecret1, client.getClientSecret1());
        assertNotNull(client.getClientSecret1Updated());

        // Set and verify secret2
        String hashedSecret2 = "hashed-secret-2";
        clientRepository.saveClientSecret2(clientId, hashedSecret2);
        client = clientRepository.getClient(clientId);
        assertEquals(hashedSecret2, client.getClientSecret2());
        assertNotNull(client.getClientSecret2Updated());

        // Clear secrets
        clientRepository.clearClientSecret1(clientId);
        client = clientRepository.getClient(clientId);
        assertNull(client.getClientSecret1());

        clientRepository.clearClientSecret2(clientId);
        client = clientRepository.getClient(clientId);
        assertNull(client.getClientSecret2());

        // Cleanup
        clientRepository.deleteClient(clientId);
    }

    @Test
    void testAuthorizationCrud() {
        // First create two clients
        String subjectClientId = "subject-client-" + System.currentTimeMillis();
        String audienceClientId = "audience-client-" + System.currentTimeMillis();
        List<ClientScope> scopes = new ArrayList<>();

        clientRepository.putClient(subjectClientId, "Subject", "salt1", ClientType.APPLICATION, scopes);
        clientRepository.putClient(audienceClientId, "Audience", "salt2", ClientType.APPLICATION, scopes);

        // Create authorization
        List<String> authorizedScopes = new ArrayList<>();
        authorizedScopes.add("read");
        authorizedScopes.add("write");
        authorizationRepository.authorize(subjectClientId, audienceClientId, authorizedScopes);

        // Read authorization
        Authorization auth = authorizationRepository.getAuthorization(subjectClientId, audienceClientId);
        assertNotNull(auth);
        assertEquals(subjectClientId, auth.getSubject());
        assertEquals(audienceClientId, auth.getAudience());
        assertEquals(2, auth.getAuthorizedScopes().size());
        assertTrue(auth.getAuthorizedScopes().contains("read"));
        assertTrue(auth.getAuthorizedScopes().contains("write"));

        // Get by subject
        Iterator<Authorization> bySubject = authorizationRepository.getAuthorizationBySubject(subjectClientId);
        assertTrue(bySubject.hasNext());
        Authorization authBySubject = bySubject.next();
        assertEquals(audienceClientId, authBySubject.getAudience());

        // Get by audience
        Iterator<Authorization> byAudience = authorizationRepository.getAuthorizationByAudience(audienceClientId);
        assertTrue(byAudience.hasNext());
        Authorization authByAudience = byAudience.next();
        assertEquals(subjectClientId, authByAudience.getSubject());

        // Deauthorize
        authorizationRepository.deauthorize(subjectClientId, audienceClientId);
        Authorization deletedAuth = authorizationRepository.getAuthorization(subjectClientId, audienceClientId);
        assertNull(deletedAuth);

        // Cleanup
        clientRepository.deleteClient(subjectClientId);
        clientRepository.deleteClient(audienceClientId);
    }

    @Test
    void testAuthorizationScopes() {
        String subjectClientId = "scope-subject-" + System.currentTimeMillis();
        String audienceClientId = "scope-audience-" + System.currentTimeMillis();
        List<ClientScope> scopes = new ArrayList<>();

        clientRepository.putClient(subjectClientId, "Subject", "salt1", ClientType.APPLICATION, scopes);
        clientRepository.putClient(audienceClientId, "Audience", "salt2", ClientType.APPLICATION, scopes);

        // Create authorization with initial scope
        List<String> initialScopes = new ArrayList<>();
        initialScopes.add("read");
        authorizationRepository.authorize(subjectClientId, audienceClientId, initialScopes);

        // Add a scope
        authorizationRepository.authorizeAddScope(subjectClientId, audienceClientId, "write");
        Authorization auth = authorizationRepository.getAuthorization(subjectClientId, audienceClientId);
        assertEquals(2, auth.getAuthorizedScopes().size());
        assertTrue(auth.getAuthorizedScopes().contains("write"));

        // Remove a scope
        authorizationRepository.authorizeRemoveScope(subjectClientId, audienceClientId, "read");
        auth = authorizationRepository.getAuthorization(subjectClientId, audienceClientId);
        assertEquals(1, auth.getAuthorizedScopes().size());
        assertFalse(auth.getAuthorizedScopes().contains("read"));
        assertTrue(auth.getAuthorizedScopes().contains("write"));

        // Cleanup
        authorizationRepository.deauthorize(subjectClientId, audienceClientId);
        clientRepository.deleteClient(subjectClientId);
        clientRepository.deleteClient(audienceClientId);
    }

    @Test
    void testJwtBearerAuthorization() {
        String clientId = "jwt-client-" + System.currentTimeMillis();
        List<ClientScope> scopes = new ArrayList<>();

        // Create client
        clientRepository.putClient(clientId, "JWT Test Client", "salt", ClientType.APPLICATION, scopes);

        // Add JWT bearer
        String id = "jwt-bearer-id";
        String jwksUrl = "https://example.com/.well-known/jwks.json";
        String iss = "https://example.com";
        String sub = "service-account";
        String aud = "https://api.example.com";

        clientRepository.addAuthorizedJwt(clientId, id, jwksUrl, iss, sub, aud);

        // Verify JWT bearer was added
        Client client = clientRepository.getClient(clientId);
        assertEquals(1, client.getJwtBearer().size());
        assertEquals(id, client.getJwtBearer().get(0).getId());
        assertEquals(jwksUrl, client.getJwtBearer().get(0).getJwksUrl());
        assertEquals(iss, client.getJwtBearer().get(0).getIss());
        assertEquals(sub, client.getJwtBearer().get(0).getSub());
        assertEquals(aud, client.getJwtBearer().get(0).getAud());

        // Remove JWT bearer
        clientRepository.removeAuthorizedJwt(clientId, id);
        client = clientRepository.getClient(clientId);
        assertEquals(0, client.getJwtBearer().size());

        // Cleanup
        clientRepository.deleteClient(clientId);
    }
}
