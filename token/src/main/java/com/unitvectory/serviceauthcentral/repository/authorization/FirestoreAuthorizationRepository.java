package com.unitvectory.serviceauthcentral.repository.authorization;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.unitvectory.serviceauthcentral.model.AuthorizationRecord;

public class FirestoreAuthorizationRepository implements AuthorizationRepository {

	@Autowired
	private Firestore firestore;

	@Override
	public AuthorizationRecord getAuthorization(String subject, String audience)
			throws InterruptedException, ExecutionException {
		if (subject == null || subject.trim().isEmpty()) {
			throw new IllegalArgumentException("subject is required");
		} else if (audience == null || audience.trim().isEmpty()) {
			throw new IllegalArgumentException("audience is required");
		}

		QuerySnapshot querySnapshot = firestore.collection("authorizations").whereEqualTo("subject", subject)
				.whereEqualTo("audience", audience).limit(1).get().get();

		// Process the query results
		List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
		if (!documents.isEmpty()) {
			// Convert the first document to a ClientRecord object
			return documents.get(0).toObject(AuthorizationRecord.class);
		} else {
			return null;
		}
	}
}
