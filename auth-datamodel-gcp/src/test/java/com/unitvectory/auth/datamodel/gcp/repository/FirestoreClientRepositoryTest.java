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
import com.unitvectory.auth.datamodel.gcp.repository.ClientRecord;
import com.unitvectory.auth.datamodel.gcp.repository.FirestoreClientRepository;
import com.unitvectory.auth.datamodel.model.Client;

public class FirestoreClientRepositoryTest {

	@Test
	public void testNoArgs() throws InterruptedException, ExecutionException {
		FirestoreClientRepository repository = new FirestoreClientRepository(null);
		assertNotNull(repository);
	}

	@Test
	public void testGetClient_NoClientId() throws InterruptedException, ExecutionException {
		Firestore firestore = Mockito.mock(Firestore.class);
		FirestoreClientRepository repository = new FirestoreClientRepository(firestore);

		NullPointerException thrown = assertThrows(NullPointerException.class, () -> repository.getClient(null),
				"Expected getClient with null clientId to throw exception");

		assertEquals("clientId is marked non-null but is null", thrown.getMessage());
	}

	@Test
	public void testGetClient_InvalidClientId() throws InterruptedException, ExecutionException {
		Firestore firestore = Mockito.mock(Firestore.class);
		FirestoreClientRepository repository = new FirestoreClientRepository(firestore);

		// Mock Firestore dependencies
		CollectionReference collectionReference = Mockito.mock(CollectionReference.class);
		DocumentReference documentReference = Mockito.mock(DocumentReference.class);
		ApiFuture<DocumentSnapshot> future = Mockito.mock(ApiFuture.class);
		DocumentSnapshot document = Mockito.mock(DocumentSnapshot.class);

		// Setup mocks
		Mockito.when(firestore.collection("clients")).thenReturn(collectionReference);
		Mockito.when(collectionReference.document("invalid-client-id")).thenReturn(documentReference);
		Mockito.when(documentReference.get()).thenReturn(future);
		Mockito.when(future.get()).thenReturn(document);
		Mockito.when(document.exists()).thenReturn(false);

		Client client = repository.getClient("invalid-client-id");
		assertNull(client);
	}

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
		Mockito.when(collectionReference.document("client-id")).thenReturn(documentReference);
		Mockito.when(documentReference.get()).thenReturn(future);
		Mockito.when(future.get()).thenReturn(document);
		Mockito.when(document.exists()).thenReturn(true);
		Mockito.when(document.toObject(ClientRecord.class)).thenReturn(new ClientRecord());

		// Use constructor injection or Spring's dependency injection
		FirestoreClientRepository repository = new FirestoreClientRepository(firestore);

		// Execute and assert
		Client client = repository.getClient("client-id");
		assertNotNull(client);
	}

}
