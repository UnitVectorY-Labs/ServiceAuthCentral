package com.unitvectory.serviceauthcentral.repository.client;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.exception.InternalServerErrorException;
import com.unitvectory.serviceauthcentral.model.ClientRecord;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class FirestoreClientRepository implements ClientRepository {

	@Autowired
	private Firestore firestore;

	@Override
	public Client getClient(String clientId) {
		if (clientId == null || clientId.trim().isEmpty()) {
			throw new IllegalArgumentException("clientId is required");
		}

		try {
			DocumentSnapshot document = firestore.collection("clients").document(clientId).get().get();
			if (document.exists()) {
				return document.toObject(ClientRecord.class);
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new InternalServerErrorException(e);
		}
	}
}
