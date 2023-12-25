package com.unitvectory.serviceauthcentral.gcp.repository;

import java.util.concurrent.ExecutionException;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.unitvectory.serviceauthcentral.datamodel.exception.ConflictException;
import com.unitvectory.serviceauthcentral.datamodel.exception.InternalServerErrorException;
import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class FirestoreClientRepository implements ClientRepository {

	private Firestore firestore;

	@Override
	public Client getClient(@NonNull String clientId) {
		try {
			DocumentSnapshot document = firestore.collection("clients").document(clientId).get().get();
			if (document.exists()) {
				return document.toObject(ClientRecord.class);
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
			System.out.println("putClient: " + clientId);
			// Reference to the document in the 'clients' collection with the specified
			// clientId
			DocumentReference document = firestore.collection("clients").document(clientId);

			// Start a Firestore transaction

			firestore.runTransaction(transaction -> {
				// Attempt to retrieve the existing document
				DocumentSnapshot snapshot = transaction.get(document).get();

				System.out.println("putClient: runTransaction");

				// If the document does not exist, create the new ClientRecord
				if (!snapshot.exists()) {

					System.out.println("putClient: not exists");
					ClientRecord record = ClientRecord.builder().documentId(clientId).clientId(clientId)
							.description(description).salt(salt).build();

					// Perform the transactional write to create the new record
					transaction.set(document, record);
				} else {

					System.out.println("putClient: exists");
					// Handle the case where the record already exists
					// For example, log a message or throw a custom exception
					throw new ConflictException("clientId already exists");
				}

				System.out.println("putClient: return");

				// Must return a result; here, null signifies nothing further to return
				return null;
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}

		System.out.println("putClient: end");
	}

	@Override
	public void saveClientSecret1(@NonNull String clientId, @NonNull String hashedSecret) {
		try {
			firestore.collection("clients").document(clientId).update("clientSecret1", hashedSecret).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void saveClientSecret2(@NonNull String clientId, @NonNull String hashedSecret) {
		try {
			firestore.collection("clients").document(clientId).update("clientSecret2", hashedSecret).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void clearClientSecret1(@NonNull String clientId) {
		try {
			firestore.collection("clients").document(clientId).update("clientSecret1", null).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void clearClientSecret2(@NonNull String clientId) {
		try {
			firestore.collection("clients").document(clientId).update("clientSecret2", null).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

}
