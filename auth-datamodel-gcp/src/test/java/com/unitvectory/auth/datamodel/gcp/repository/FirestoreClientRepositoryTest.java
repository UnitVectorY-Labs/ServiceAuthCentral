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
package com.unitvectory.auth.datamodel.gcp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.unitvectory.auth.common.service.time.StaticTimeService;
import com.unitvectory.auth.common.service.time.TimeService;
import com.unitvectory.auth.datamodel.gcp.model.ClientRecord;
import com.unitvectory.auth.datamodel.model.Client;
import com.unitvectory.auth.util.HashingUtil;

/**
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@SuppressWarnings("unchecked")
public class FirestoreClientRepositoryTest {

	private static final String CLIENTS = "clients";

	private final TimeService timeService = new StaticTimeService(0);

	@Test
	public void testNoArgs() throws InterruptedException, ExecutionException {
		FirestoreClientRepository repository =
				new FirestoreClientRepository(null, CLIENTS, timeService);
		assertNotNull(repository);
	}

	@Test
	public void testGetClient_NoClientId() throws InterruptedException, ExecutionException {
		Firestore firestore = Mockito.mock(Firestore.class);
		FirestoreClientRepository repository =
				new FirestoreClientRepository(firestore, CLIENTS, timeService);

		NullPointerException thrown =
				assertThrows(NullPointerException.class, () -> repository.getClient(null),
						"Expected getClient with null clientId to throw exception");

		assertEquals("clientId is marked non-null but is null", thrown.getMessage());
	}

	@SuppressWarnings("null")
	@Test
	public void testGetClient_InvalidClientId() throws InterruptedException, ExecutionException {
		Firestore firestore = Mockito.mock(Firestore.class);
		FirestoreClientRepository repository =
				new FirestoreClientRepository(firestore, CLIENTS, timeService);

		// Mock Firestore dependencies
		CollectionReference collectionReference = Mockito.mock(CollectionReference.class);
		DocumentReference documentReference = Mockito.mock(DocumentReference.class);
		ApiFuture<DocumentSnapshot> future = Mockito.mock(ApiFuture.class);
		DocumentSnapshot document = Mockito.mock(DocumentSnapshot.class);

		// Setup mocks
		Mockito.when(firestore.collection("clients")).thenReturn(collectionReference);
		Mockito.when(collectionReference.document(HashingUtil.sha256("invalid-client-id")))
				.thenReturn(documentReference);
		Mockito.when(documentReference.get()).thenReturn(future);
		Mockito.when(future.get()).thenReturn(document);
		Mockito.when(document.exists()).thenReturn(false);

		Client client = repository.getClient("invalid-client-id");
		assertNull(client);
	}

	@SuppressWarnings("null")
	@Test
	public void testGetClient_ClientExists() throws InterruptedException, ExecutionException {
		Firestore firestore = Mockito.mock(Firestore.class);

		// Mock Firestore dependencies
		CollectionReference collectionReference = Mockito.mock(CollectionReference.class);
		DocumentReference documentReference = Mockito.mock(DocumentReference.class);
		ApiFuture<DocumentSnapshot> future = Mockito.mock(ApiFuture.class);
		DocumentSnapshot document = Mockito.mock(DocumentSnapshot.class);

		// Setup mocks
		Mockito.when(firestore.collection("clients")).thenReturn(collectionReference);
		Mockito.when(collectionReference.document(HashingUtil.sha256("client-id")))
				.thenReturn(documentReference);
		Mockito.when(documentReference.get()).thenReturn(future);
		Mockito.when(future.get()).thenReturn(document);
		Mockito.when(document.exists()).thenReturn(true);
		Mockito.when(document.toObject(ClientRecord.class)).thenReturn(new ClientRecord());

		// Use constructor injection or Spring's dependency injection
		FirestoreClientRepository repository =
				new FirestoreClientRepository(firestore, CLIENTS, timeService);

		// Execute and assert
		Client client = repository.getClient("client-id");
		assertNotNull(client);
	}

}
