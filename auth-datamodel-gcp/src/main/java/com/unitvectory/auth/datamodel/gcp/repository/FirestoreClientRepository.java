package com.unitvectory.auth.datamodel.gcp.repository;

import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.unitvectory.auth.datamodel.gcp.mapper.ClientRecordMapper;
import com.unitvectory.auth.datamodel.gcp.model.ClientRecord;
import com.unitvectory.auth.datamodel.model.Client;
import com.unitvectory.auth.datamodel.repository.ClientRepository;
import com.unitvectory.auth.util.exception.ConflictException;
import com.unitvectory.auth.util.exception.InternalServerErrorException;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@SuppressWarnings("null")
@AllArgsConstructor
public class FirestoreClientRepository implements ClientRepository {

	private static final String CLIENTSECRET1 = "clientSecret1";

	private static final String CLIENTSECRET2 = "clientSecret2";

	private Firestore firestore;

	private String collectionClients;

	@Override
	public Client getClient(@NonNull String clientId) {
		try {
			DocumentSnapshot document = firestore.collection(this.collectionClients).document(clientId).get().get();
			if (document.exists()) {
				return ClientRecordMapper.INSTANCE.documentSnapshotToClientRecord(document);
			} else {
				return null;
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void putClient(@NonNull String clientId, String description, @NonNull String salt) {

		try {
			// Reference to the document in the 'clients' collection with the specified
			// clientId
			DocumentReference document = firestore.collection(this.collectionClients).document(clientId);

			// Start a Firestore transaction

			firestore.runTransaction(transaction -> {
				// Attempt to retrieve the existing document
				DocumentSnapshot snapshot = transaction.get(document).get();

				// If the document does not exist, create the new ClientRecord
				if (!snapshot.exists()) {
					@Nonnull
					ClientRecord record = ClientRecord.builder().documentId(clientId).clientId(clientId)
							.description(description).salt(salt).build();

					// Perform the transactional write to create the new record
					transaction.set(document, record);
				} else {
					// Handle the case where the record already exists
					// For example, log a message or throw a custom exception
					throw new ConflictException("clientId already exists");
				}

				// Must return a result; here, null signifies nothing further to return
				return null;
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void saveClientSecret1(@NonNull String clientId, @NonNull String hashedSecret) {
		try {
			firestore.collection(this.collectionClients).document(clientId).update(CLIENTSECRET1, hashedSecret).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void saveClientSecret2(@NonNull String clientId, @NonNull String hashedSecret) {
		try {
			firestore.collection(this.collectionClients).document(clientId).update(CLIENTSECRET2, hashedSecret).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void clearClientSecret1(@NonNull String clientId) {
		try {
			firestore.collection(this.collectionClients).document(clientId).update(CLIENTSECRET1, null).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void clearClientSecret2(@NonNull String clientId) {
		try {
			firestore.collection(this.collectionClients).document(clientId).update(CLIENTSECRET2, null).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

}
