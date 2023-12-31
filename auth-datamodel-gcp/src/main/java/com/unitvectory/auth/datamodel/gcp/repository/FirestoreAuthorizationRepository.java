package com.unitvectory.auth.datamodel.gcp.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.unitvectory.auth.datamodel.gcp.model.AuthorizationRecord;
import com.unitvectory.auth.datamodel.model.Authorization;
import com.unitvectory.auth.datamodel.repository.AuthorizationRepository;
import com.unitvectory.auth.util.HashingUtil;
import com.unitvectory.auth.util.exception.InternalServerErrorException;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@SuppressWarnings("null")
@AllArgsConstructor
public class FirestoreAuthorizationRepository implements AuthorizationRepository {

	private static final String AUDIENCE = "audience";

	private static final String SUBJECT = "subject";

	private Firestore firestore;

	private String collectionAuthorizations;

	@Override
	public Authorization getAuthorization(@NonNull String id) {
		try {
			DocumentSnapshot document = firestore.collection(this.collectionAuthorizations).document(id).get().get();
			if (document.exists()) {
				return document.toObject(AuthorizationRecord.class);
			} else {
				return null;
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public Authorization getAuthorization(@NonNull String subject, @NonNull String audience) {
		try {
			QuerySnapshot querySnapshot = firestore.collection(this.collectionAuthorizations)
					.whereEqualTo(SUBJECT, subject).whereEqualTo(AUDIENCE, audience).limit(1).get().get();

			// Process the query results
			List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
			if (!documents.isEmpty()) {
				// Convert the first document to a ClientRecord object
				return documents.get(0).toObject(AuthorizationRecord.class);
			} else {
				return null;
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public Iterator<Authorization> getAuthorizationBySubject(@NonNull String subject) {
		try {
			QuerySnapshot querySnapshot = firestore.collection(this.collectionAuthorizations)
					.whereEqualTo(SUBJECT, subject).get().get();

			ArrayList<Authorization> list = new ArrayList<>();

			List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
			for (QueryDocumentSnapshot document : documents) {
				list.add(document.toObject(AuthorizationRecord.class));
			}

			return list.iterator();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public Iterator<Authorization> getAuthorizationByAudience(@NonNull String audience) {
		try {
			QuerySnapshot querySnapshot = firestore.collection(this.collectionAuthorizations)
					.whereEqualTo(AUDIENCE, audience).get().get();

			ArrayList<Authorization> list = new ArrayList<>();

			List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
			for (QueryDocumentSnapshot document : documents) {
				list.add(document.toObject(AuthorizationRecord.class));
			}

			return list.iterator();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void authorize(@NonNull String subject, @NonNull String audience) {
		AuthorizationRecord record = AuthorizationRecord.builder().subject(subject).audience(audience).build();
		String documentId = getDocumentId(subject, audience);
		DocumentReference docRef = this.firestore.collection(this.collectionAuthorizations).document(documentId);
		docRef.set(record);
	}

	@Override
	public void deauthorize(@NonNull String subject, @NonNull String audience) {
		String documentId = getDocumentId(subject, audience);
		DocumentReference docRef = this.firestore.collection(this.collectionAuthorizations).document(documentId);
		docRef.delete();
	}

	private String getDocumentId(@NonNull String subject, @NonNull String audience) {
		String subjectHash = HashingUtil.sha256(subject);
		String audienceHash = HashingUtil.sha256(audience);
		return HashingUtil.sha256(subjectHash + audienceHash);
	}
}
