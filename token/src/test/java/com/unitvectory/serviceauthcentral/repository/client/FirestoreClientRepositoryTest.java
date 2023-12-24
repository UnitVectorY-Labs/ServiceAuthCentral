package com.unitvectory.serviceauthcentral.repository.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.unitvectory.serviceauthcentral.config.TestGcpConfig;
import com.unitvectory.serviceauthcentral.config.TestServiceAuthCentralConfig;
import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.model.ClientRecord;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:test-application.properties")
@Import({ TestGcpConfig.class, TestServiceAuthCentralConfig.class })
public class FirestoreClientRepositoryTest {

	@MockBean
	private Firestore firestore;

	@Test
	public void testNoArgs() throws InterruptedException, ExecutionException {
		FirestoreClientRepository repository = new FirestoreClientRepository();
		assertNotNull(repository);
	}

	@Test
	public void testGetClient_NoClientId() throws InterruptedException, ExecutionException {
		FirestoreClientRepository repository = new FirestoreClientRepository(firestore);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> repository.getClient(null),
				"Expected getClient with null clientId to throw exception");

		assertTrue(thrown.getMessage().equals("clientId is required"));
	}

	@Test
	public void testGetClient_EmptyClientId() throws InterruptedException, ExecutionException {
		FirestoreClientRepository repository = new FirestoreClientRepository(firestore);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> repository.getClient(""),
				"Expected getClient with null clientId to throw exception");

		assertTrue(thrown.getMessage().equals("clientId is required"));
	}

	@Test
	public void testGetClient_InvalidClientId() throws InterruptedException, ExecutionException {
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
