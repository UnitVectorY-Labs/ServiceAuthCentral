package com.unitvectory.auth.datamodel.gcp.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.unitvectory.auth.datamodel.gcp.model.ClientJwtBearerRecord;
import com.unitvectory.auth.datamodel.gcp.model.ClientRecord;
import com.unitvectory.auth.datamodel.gcp.model.ClientSummaryRecord;
import com.unitvectory.auth.datamodel.model.Client;
import com.unitvectory.auth.datamodel.model.ClientJwtBearer;
import com.unitvectory.auth.datamodel.model.ClientSummary;
import com.unitvectory.auth.datamodel.repository.ClientRepository;
import com.unitvectory.auth.util.exception.BadRequestException;
import com.unitvectory.auth.util.exception.ConflictException;
import com.unitvectory.auth.util.exception.InternalServerErrorException;
import com.unitvectory.auth.util.exception.NotFoundException;

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
	public List<ClientSummary> getClients() {

		try {
			QuerySnapshot querySnapshot = firestore.collection(this.collectionClients)
					.select("clientId", "description").get().get();

			ArrayList<ClientSummary> list = new ArrayList<>();

			List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
			for (QueryDocumentSnapshot document : documents) {
				list.add(document.toObject(ClientSummaryRecord.class));
			}

			return list;
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public Client getClient(@NonNull String clientId) {
		try {
			DocumentSnapshot document =
					firestore.collection(this.collectionClients).document(clientId).get().get();
			if (document.exists()) {
				ClientRecord record = document.toObject(ClientRecord.class);
				return record;
			} else {
				return null;
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void deleteClient(@NonNull String clientId) {
		try {
			this.firestore.collection(this.collectionClients).document(clientId).delete().get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void putClient(@NonNull String clientId, String description, @NonNull String salt) {

		try {
			// Reference to the document in the 'clients' collection with the specified
			// clientId
			DocumentReference document =
					firestore.collection(this.collectionClients).document(clientId);

			// Start a Firestore transaction

			firestore.runTransaction(transaction -> {
				// Attempt to retrieve the existing document
				DocumentSnapshot snapshot = transaction.get(document).get();

				// If the document does not exist, create the new ClientRecord
				if (!snapshot.exists()) {
					@Nonnull
					ClientRecord record = ClientRecord.builder().documentId(clientId)
							.clientId(clientId).description(description).salt(salt).build();

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
	public void addAuthorizedJwt(@NonNull String clientId, @NonNull String id,
			@NonNull String jwksUrl, @NonNull String iss, @NonNull String sub,
			@NonNull String aud) {
		try {
			DocumentReference docRef =
					firestore.collection(this.collectionClients).document(clientId);
			firestore.runTransaction(transaction -> {
				DocumentSnapshot snapshot = transaction.get(docRef).get();

				if (!snapshot.exists()) {
					throw new NotFoundException("Client not found");
				}

				ClientRecord record = snapshot.toObject(ClientRecord.class);
				List<ClientJwtBearer> jwtBearerListOriginal = record.getJwtBearer();
				List<ClientJwtBearer> jwtBearerList = new ArrayList<>();

				// If the original list is not null, add all its elements to the new list
				if (jwtBearerListOriginal != null) {
					jwtBearerList.addAll(jwtBearerListOriginal);
				}

				// Constructing new JWT object
				ClientJwtBearerRecord newJwt = ClientJwtBearerRecord.builder().id(id)
						.jwksUrl(jwksUrl).iss(iss).sub(sub).aud(aud).build();

				// Check for duplicates
				for (ClientJwtBearer cjb : jwtBearerList) {
					if (newJwt.matches(cjb)) {
						throw new BadRequestException("Duplicate authorization");
					}
				}

				// Add the new JWT to the list and update the document
				jwtBearerList.add(newJwt);
				transaction.update(docRef, "jwtBearer", jwtBearerList);

				return null; // Firestore transactions require that you return something
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void removeAuthorizedJwt(@NonNull String clientId, @NonNull String id) {
		try {
			DocumentReference docRef =
					firestore.collection(this.collectionClients).document(clientId);
			firestore.runTransaction(transaction -> {
				DocumentSnapshot snapshot = transaction.get(docRef).get();

				if (!snapshot.exists()) {
					throw new NotFoundException("Client not found");
				}

				ClientRecord record = snapshot.toObject(ClientRecord.class);

				List<ClientJwtBearer> jwtBearerListOriginal = record.getJwtBearer();
				List<ClientJwtBearer> jwtBearerList = new ArrayList<>();

				if (jwtBearerListOriginal != null) {
					// Copy existing JWTs into a modifiable list
					jwtBearerList.addAll(jwtBearerListOriginal);

					// Find the JWT to remove
					ClientJwtBearer jwtToRemove = null;
					for (ClientJwtBearer cjb : jwtBearerList) {
						if (id.equals(cjb.getId())) {
							jwtToRemove = cjb;
							break;
						}
					}

					// Remove the JWT if found
					if (jwtToRemove != null) {
						jwtBearerList.remove(jwtToRemove);
						// Update the document
						transaction.update(docRef, "jwtBearer", jwtBearerList);
					} else {
						throw new NotFoundException("JWT not found with the provided id");
					}
				}

				return null; // Firestore transactions require that you return something
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void saveClientSecret1(@NonNull String clientId, @NonNull String hashedSecret) {
		try {
			firestore.collection(this.collectionClients).document(clientId)
					.update(CLIENTSECRET1, hashedSecret).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void saveClientSecret2(@NonNull String clientId, @NonNull String hashedSecret) {
		try {
			firestore.collection(this.collectionClients).document(clientId)
					.update(CLIENTSECRET2, hashedSecret).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void clearClientSecret1(@NonNull String clientId) {
		try {
			firestore.collection(this.collectionClients).document(clientId)
					.update(CLIENTSECRET1, null).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public void clearClientSecret2(@NonNull String clientId) {
		try {
			firestore.collection(this.collectionClients).document(clientId)
					.update(CLIENTSECRET2, null).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new InternalServerErrorException(e);
		}
	}
}
