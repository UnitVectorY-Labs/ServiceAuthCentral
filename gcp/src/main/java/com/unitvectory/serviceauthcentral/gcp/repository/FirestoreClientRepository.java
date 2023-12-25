package com.unitvectory.serviceauthcentral.gcp.repository;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.unitvectory.serviceauthcentral.datamodel.exception.InternalServerErrorException;
import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@AllArgsConstructor
public class FirestoreClientRepository implements ClientRepository {

	@Autowired
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
			DocumentSnapshot document = firestore.collection("clients").document(clientId).get().get();
			if (document.exists()) {

			} else {

			}
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
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
			firestore.collection("clients").document(clientId).update("clientSecret1", FieldValue.delete()).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void clearClientSecret2(@NonNull String clientId) {
		try {
			firestore.collection("clients").document(clientId).update("clientSecret2", FieldValue.delete()).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

}
