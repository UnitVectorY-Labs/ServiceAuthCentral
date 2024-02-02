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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.unitvectory.auth.datamodel.gcp.model.AuthorizationRecord;
import com.unitvectory.auth.datamodel.model.Authorization;
import com.unitvectory.auth.util.exception.InternalServerErrorException;

/**
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@SuppressWarnings({"unchecked", "null"})
public class FirestoreAuthorizationRepositoryTest {

	private static final String COLLECTION_AUTHORIZATIONS = "authorizations";

	private Firestore mockFirestore() throws InterruptedException, ExecutionException {
		Firestore firestore = mock(Firestore.class);
		CollectionReference collection = mock(CollectionReference.class);
		DocumentReference documentRef = mock(DocumentReference.class);
		ApiFuture<DocumentSnapshot> futureSnapshot = mock(ApiFuture.class);
		ApiFuture<QuerySnapshot> futureQuery = mock(ApiFuture.class);
		Query query = mock(Query.class);

		when(firestore.collection(COLLECTION_AUTHORIZATIONS)).thenReturn(collection);
		when(collection.document(anyString())).thenReturn(documentRef);
		when(documentRef.get()).thenReturn(futureSnapshot);
		when(collection.whereEqualTo(anyString(), any())).thenReturn(query);
		when(query.whereEqualTo(anyString(), any())).thenReturn(query);
		when(query.limit(anyInt())).thenReturn(query); // Ensure this returns a mock Query
		when(query.get()).thenReturn(futureQuery);

		return firestore;
	}

	@Test
	public void testGetAuthorizationById_Exists() throws InterruptedException, ExecutionException {
		Firestore firestore = mockFirestore();
		DocumentSnapshot document = mock(DocumentSnapshot.class);
		when(firestore.collection(COLLECTION_AUTHORIZATIONS).document(anyString()).get().get())
				.thenReturn(document);
		when(document.exists()).thenReturn(true);
		when(document.toObject(AuthorizationRecord.class)).thenReturn(new AuthorizationRecord());

		FirestoreAuthorizationRepository repository =
				new FirestoreAuthorizationRepository(firestore, COLLECTION_AUTHORIZATIONS);
		Authorization authorization = repository.getAuthorization("some-id");

		assertNotNull(authorization);
	}

	@Test
	public void testGetAuthorizationById_NotExists()
			throws InterruptedException, ExecutionException {
		Firestore firestore = mockFirestore();
		DocumentSnapshot document = mock(DocumentSnapshot.class);
		when(firestore.collection(COLLECTION_AUTHORIZATIONS).document(anyString()).get().get())
				.thenReturn(document);
		when(document.exists()).thenReturn(false);

		FirestoreAuthorizationRepository repository =
				new FirestoreAuthorizationRepository(firestore, COLLECTION_AUTHORIZATIONS);
		Authorization authorization = repository.getAuthorization("some-id");

		assertNull(authorization);
	}

	@Test
	public void testGetAuthorizationById_ThrowsException()
			throws InterruptedException, ExecutionException {
		Firestore firestore = mockFirestore();
		when(firestore.collection(COLLECTION_AUTHORIZATIONS).document(anyString()).get().get())
				.thenThrow(new InterruptedException());

		FirestoreAuthorizationRepository repository =
				new FirestoreAuthorizationRepository(firestore, COLLECTION_AUTHORIZATIONS);

		assertThrows(InternalServerErrorException.class,
				() -> repository.getAuthorization("some-id"));
	}

	@Test
	public void testGetAuthorizationBySubjectAndAudience_Exists()
			throws InterruptedException, ExecutionException {
		Firestore firestore = mockFirestore();
		QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
		QueryDocumentSnapshot queryDocument = mock(QueryDocumentSnapshot.class);
		when(firestore.collection(COLLECTION_AUTHORIZATIONS).whereEqualTo(anyString(), any()).get()
				.get()).thenReturn(querySnapshot);
		when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(queryDocument));
		when(queryDocument.toObject(AuthorizationRecord.class))
				.thenReturn(new AuthorizationRecord());

		FirestoreAuthorizationRepository repository =
				new FirestoreAuthorizationRepository(firestore, COLLECTION_AUTHORIZATIONS);
		Authorization authorization = repository.getAuthorization("subject", "audience");

		assertNotNull(authorization);
	}

	@Test
	public void testGetAuthorizationBySubjectAndAudience_NotExists()
			throws InterruptedException, ExecutionException {
		Firestore firestore = mockFirestore();
		QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
		when(firestore.collection(COLLECTION_AUTHORIZATIONS).whereEqualTo(anyString(), any()).get()
				.get()).thenReturn(querySnapshot);
		when(querySnapshot.getDocuments()).thenReturn(Arrays.asList());

		FirestoreAuthorizationRepository repository =
				new FirestoreAuthorizationRepository(firestore, COLLECTION_AUTHORIZATIONS);
		Authorization authorization = repository.getAuthorization("subject", "audience");

		assertNull(authorization);
	}

	@Test
	public void testGetAuthorizationBySubjectAndAudience_ThrowsException()
			throws InterruptedException, ExecutionException {
		Firestore firestore = mockFirestore();
		when(firestore.collection(COLLECTION_AUTHORIZATIONS).whereEqualTo(anyString(), any()).get()
				.get()).thenThrow(new InterruptedException());

		FirestoreAuthorizationRepository repository =
				new FirestoreAuthorizationRepository(firestore, COLLECTION_AUTHORIZATIONS);

		assertThrows(InternalServerErrorException.class,
				() -> repository.getAuthorization("subject", "audience"));
	}

	// Similar tests for getAuthorizationBySubject and getAuthorizationByAudience
	// ...

	@Test
	public void testAuthorize() throws InterruptedException, ExecutionException {
		Firestore firestore = mockFirestore();
		DocumentReference documentRef = mock(DocumentReference.class);
		when(firestore.collection(COLLECTION_AUTHORIZATIONS).document(anyString()))
				.thenReturn(documentRef);
		ApiFuture<WriteResult> future = mock(ApiFuture.class);
		when(documentRef.set(any(AuthorizationRecord.class))).thenReturn(future);

		FirestoreAuthorizationRepository repository =
				new FirestoreAuthorizationRepository(firestore, COLLECTION_AUTHORIZATIONS);
		repository.authorize("subject", "audience");

		ArgumentCaptor<AuthorizationRecord> argument =
				ArgumentCaptor.forClass(AuthorizationRecord.class);
		verify(documentRef).set(argument.capture());
		assertEquals("subject", argument.getValue().getSubject());
		assertEquals("audience", argument.getValue().getAudience());
	}

	@Test
	public void testDeauthorize() throws InterruptedException, ExecutionException {
		Firestore firestore = mockFirestore();
		DocumentReference documentRef = mock(DocumentReference.class);
		when(firestore.collection(COLLECTION_AUTHORIZATIONS).document(anyString()))
				.thenReturn(documentRef);
		ApiFuture<WriteResult> future = mock(ApiFuture.class);
		when(documentRef.delete()).thenReturn(future);

		FirestoreAuthorizationRepository repository =
				new FirestoreAuthorizationRepository(firestore, COLLECTION_AUTHORIZATIONS);
		repository.deauthorize("subject", "audience");

		verify(documentRef).delete();
	}
}
