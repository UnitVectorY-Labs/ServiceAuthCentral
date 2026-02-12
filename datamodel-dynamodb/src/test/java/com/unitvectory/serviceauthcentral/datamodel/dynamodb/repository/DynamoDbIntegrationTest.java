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
package com.unitvectory.serviceauthcentral.datamodel.dynamodb.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import com.unitvectory.consistgen.epoch.StaticEpochTimeProvider;
import com.unitvectory.serviceauthcentral.datamodel.dynamodb.model.ClientScopeRecord;
import com.unitvectory.serviceauthcentral.datamodel.model.Authorization;
import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientScope;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientSummaryConnection;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientType;
import com.unitvectory.serviceauthcentral.datamodel.model.LoginCode;
import com.unitvectory.serviceauthcentral.datamodel.model.LoginState;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * Integration tests for DynamoDB repositories against DynamoDB Local
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@EnabledIfSystemProperty(named = "dynamodb.local.enabled", matches = "true")
public class DynamoDbIntegrationTest {

    private static DynamoDbEnhancedClient enhancedClient;
    
    @BeforeAll
    static void setup() {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create("http://localhost:8000"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("dummy", "dummy")))
                .build();

        enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Test
    void testClientRepository() {
        DynamoDbClientRepository repo = new DynamoDbClientRepository(
                enhancedClient, "sac-clients", StaticEpochTimeProvider.getInstance());

        // Create a client
        List<ClientScope> scopes = new ArrayList<>();
        scopes.add(ClientScopeRecord.builder().scope("read").description("Read access").build());
        
        repo.putClient("test-client-1", "Test Client 1", "salt123", ClientType.APPLICATION, scopes);

        // Get the client
        Client client = repo.getClient("test-client-1");
        assertNotNull(client);
        assertEquals("test-client-1", client.getClientId());
        assertEquals("Test Client 1", client.getDescription());
        assertEquals(ClientType.APPLICATION, client.getClientType());
        assertEquals("salt123", client.getSalt());

        // List clients
        ClientSummaryConnection connection = repo.getClients(10, null, null, null);
        assertNotNull(connection);
        assertTrue(connection.getEdges().size() > 0);

        // Add a secret
        repo.saveClientSecret1("test-client-1", "hashed-secret-1");
        client = repo.getClient("test-client-1");
        assertEquals("hashed-secret-1", client.getClientSecret1());

        // Clear the secret
        repo.clearClientSecret1("test-client-1");
        client = repo.getClient("test-client-1");
        assertNull(client.getClientSecret1());

        // Delete the client
        repo.deleteClient("test-client-1");
        client = repo.getClient("test-client-1");
        assertNull(client);

        System.out.println("ClientRepository tests passed!");
    }

    @Test
    void testAuthorizationRepository() {
        DynamoDbAuthorizationRepository repo = new DynamoDbAuthorizationRepository(
                enhancedClient, "sac-authorizations", StaticEpochTimeProvider.getInstance());

        // Create authorization
        repo.authorize("subject-1", "audience-1", Arrays.asList("read", "write"));

        // Get by subject and audience
        Authorization auth = repo.getAuthorization("subject-1", "audience-1");
        assertNotNull(auth);
        assertEquals("subject-1", auth.getSubject());
        assertEquals("audience-1", auth.getAudience());
        assertTrue(auth.getAuthorizedScopes().contains("read"));
        assertTrue(auth.getAuthorizedScopes().contains("write"));

        // Get by subject
        Iterator<Authorization> bySubject = repo.getAuthorizationBySubject("subject-1");
        assertTrue(bySubject.hasNext());
        assertEquals("subject-1", bySubject.next().getSubject());

        // Get by audience
        Iterator<Authorization> byAudience = repo.getAuthorizationByAudience("audience-1");
        assertTrue(byAudience.hasNext());
        assertEquals("audience-1", byAudience.next().getAudience());

        // Add scope
        repo.authorizeAddScope("subject-1", "audience-1", "delete");
        auth = repo.getAuthorization("subject-1", "audience-1");
        assertTrue(auth.getAuthorizedScopes().contains("delete"));

        // Remove scope
        repo.authorizeRemoveScope("subject-1", "audience-1", "delete");
        auth = repo.getAuthorization("subject-1", "audience-1");
        assertFalse(auth.getAuthorizedScopes().contains("delete"));

        // Deauthorize
        repo.deauthorize("subject-1", "audience-1");
        auth = repo.getAuthorization("subject-1", "audience-1");
        assertNull(auth);

        System.out.println("AuthorizationRepository tests passed!");
    }

    @Test
    void testLoginStateRepository() {
        DynamoDbLoginStateRepository repo = new DynamoDbLoginStateRepository(
                enhancedClient, "sac-loginStates");

        // Save state
        repo.saveState("session-1", "client-1", "http://redirect", "primary-state", 
                "code-challenge", "secondary-state", System.currentTimeMillis() + 3600000);

        // Get state
        LoginState state = repo.getState("session-1");
        assertNotNull(state);
        assertEquals("client-1", state.getClientId());
        assertEquals("http://redirect", state.getRedirectUri());
        assertEquals("primary-state", state.getPrimaryState());
        assertEquals("code-challenge", state.getPrimaryCodeChallenge());
        assertEquals("secondary-state", state.getSecondaryState());

        // Delete state
        repo.deleteState("session-1");
        state = repo.getState("session-1");
        assertNull(state);

        System.out.println("LoginStateRepository tests passed!");
    }

    @Test
    void testLoginCodeRepository() {
        DynamoDbLoginCodeRepository repo = new DynamoDbLoginCodeRepository(
                enhancedClient, "sac-loginCodes");

        // Save code
        repo.saveCode("auth-code-1", "client-1", "http://redirect", 
                "code-challenge", "user-client-1", System.currentTimeMillis() + 3600000);

        // Get code
        LoginCode code = repo.getCode("auth-code-1");
        assertNotNull(code);
        assertEquals("client-1", code.getClientId());
        assertEquals("http://redirect", code.getRedirectUri());
        assertEquals("code-challenge", code.getCodeChallenge());
        assertEquals("user-client-1", code.getUserClientId());

        // Delete code
        repo.deleteCode("auth-code-1");
        code = repo.getCode("auth-code-1");
        assertNull(code);

        System.out.println("LoginCodeRepository tests passed!");
    }

    @Test
    void testJwkCacheRepository() {
        DynamoDbJwkCacheRepository repo = new DynamoDbJwkCacheRepository(
                enhancedClient, "sac-keys");

        // Create a mock JWK
        com.unitvectory.serviceauthcentral.datamodel.dynamodb.model.CachedJwkRecord jwk = 
            com.unitvectory.serviceauthcentral.datamodel.dynamodb.model.CachedJwkRecord.builder()
                .kid("kid-1")
                .kty("RSA")
                .alg("RS256")
                .use("sig")
                .n("modulus-value")
                .e("AQAB")
                .valid(true)
                .build();

        long ttl = System.currentTimeMillis() / 1000 + 3600;

        // Cache the JWK
        repo.cacheJwk("https://example.com/.well-known/jwks.json", jwk, ttl);

        // Get the JWK by URL and kid
        com.unitvectory.serviceauthcentral.datamodel.model.CachedJwk cachedJwk = 
            repo.getJwk("https://example.com/.well-known/jwks.json", "kid-1");
        assertNotNull(cachedJwk);
        assertEquals("kid-1", cachedJwk.getKid());
        assertEquals("RSA", cachedJwk.getKty());
        assertEquals("RS256", cachedJwk.getAlg());
        assertTrue(cachedJwk.isValid());

        // Get JWKs by URL
        java.util.List<com.unitvectory.serviceauthcentral.datamodel.model.CachedJwk> jwks = 
            repo.getJwks("https://example.com/.well-known/jwks.json");
        assertNotNull(jwks);
        assertTrue(jwks.size() > 0);
        assertEquals("kid-1", jwks.get(0).getKid());

        // Cache absent JWK
        repo.cacheJwkAbsent("https://example.com/.well-known/jwks.json", "kid-absent", ttl);
        cachedJwk = repo.getJwk("https://example.com/.well-known/jwks.json", "kid-absent");
        assertNotNull(cachedJwk);
        assertFalse(cachedJwk.isValid());

        System.out.println("JwkCacheRepository tests passed!");
    }
}
