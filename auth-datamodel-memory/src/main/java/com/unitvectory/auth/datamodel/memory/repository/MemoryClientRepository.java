package com.unitvectory.auth.datamodel.memory.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.unitvectory.auth.datamodel.memory.model.MemoryClient;
import com.unitvectory.auth.datamodel.memory.model.MemoryClientJwtBearer;
import com.unitvectory.auth.datamodel.model.Client;
import com.unitvectory.auth.datamodel.model.ClientJwtBearer;
import com.unitvectory.auth.datamodel.repository.ClientRepository;
import com.unitvectory.auth.util.exception.BadRequestException;
import com.unitvectory.auth.util.exception.NotFoundException;

import lombok.NonNull;
import lombok.Synchronized;

public class MemoryClientRepository implements ClientRepository {

	private Map<String, MemoryClient> memory;

	public MemoryClientRepository() {
		this.memory = new HashMap<>();
	}

	public void reset() {
		this.memory.clear();
	}

	@Override
	public Client getClient(@NonNull String clientId) {
		return this.memory.get(clientId);
	}

	@Override
	@Synchronized
	public void putClient(@NonNull String clientId, String description, String salt) {
		MemoryClient record = this.memory.get(clientId);
		if (record == null) {
			record = MemoryClient.builder().clientId(clientId).description(description).salt(salt).build();
			this.memory.put(clientId, record);
		} else {
			throw new BadRequestException("client record already exists");
		}
	}

	@Override
	public void deleteClient(@NonNull String clientId) {
		this.memory.remove(clientId);
	}

	@Override
	@Synchronized
	public void addAuthorizedJwt(@NonNull String clientId, @NonNull String id, @NonNull String jwksUrl,
			@NonNull String iss, @NonNull String sub, @NonNull String aud) {
		MemoryClient record = this.memory.get(clientId);
		if (record == null) {
			throw new NotFoundException("client not found");
		}

		// What we are trying to add
		MemoryClientJwtBearer jwt = MemoryClientJwtBearer.builder().id(id).jwksUrl(jwksUrl).iss(iss).sub(sub).aud(aud)
				.build();

		List<ClientJwtBearer> list = record.getJwtBearer();

		if (list == null) {
			list = new ArrayList<>();
		} else {
			for (ClientJwtBearer cjb : record.getJwtBearer()) {
				if (jwt.matches(cjb)) {
					throw new BadRequestException("duplicate authorization");
				}
			}

			// Make a copy of the array that can be edited
			list = new ArrayList<>();
			list.addAll(record.getJwtBearer());
		}

		list.add(jwt);

		// Clone the record, modify the list
		record = record.toBuilder().jwtBearer(Collections.unmodifiableList(list)).build();
		this.memory.put(clientId, record);
	}

	@Override
	@Synchronized
	public void removeAuthorizedJwt(@NonNull String clientId, @NonNull String id) {
		MemoryClient record = this.memory.get(clientId);
		if (record == null) {
			throw new NotFoundException("client not found");
		}

		ClientJwtBearer match = null;
		if (record.getJwtBearer() != null) {
			for (ClientJwtBearer cjb : record.getJwtBearer()) {
				if (id.equals(cjb.getId())) {
					match = cjb;
				}
			}
		}

		if (match != null) {
			List<ClientJwtBearer> list = new ArrayList<>();
			list.addAll(record.getJwtBearer());
			list.remove(match);

			record = record.toBuilder().jwtBearer(list).build();
			this.memory.put(clientId, record);
		}
	}

	@Override
	@Synchronized
	public void saveClientSecret1(@NonNull String clientId, @NonNull String hashedSecret) {
		MemoryClient record = this.memory.get(clientId);
		if (record == null) {
			throw new NotFoundException("client not found");
		}

		record = record.toBuilder().clientSecret1(hashedSecret).build();
		this.memory.put(clientId, record);
	}

	@Override
	@Synchronized
	public void saveClientSecret2(@NonNull String clientId, @NonNull String hashedSecret) {
		MemoryClient record = this.memory.get(clientId);
		if (record == null) {
			throw new NotFoundException("client not found");
		}

		record = record.toBuilder().clientSecret2(hashedSecret).build();
		this.memory.put(clientId, record);
	}

	@Override
	@Synchronized
	public void clearClientSecret1(@NonNull String clientId) {
		MemoryClient record = this.memory.get(clientId);
		if (record == null) {
			throw new NotFoundException("client not found");
		}

		record = record.toBuilder().clientSecret1(null).build();
		this.memory.put(clientId, record);
	}

	@Override
	@Synchronized
	public void clearClientSecret2(@NonNull String clientId) {
		MemoryClient record = this.memory.get(clientId);
		if (record == null) {
			throw new NotFoundException("client not found");
		}

		record = record.toBuilder().clientSecret2(null).build();
		this.memory.put(clientId, record);
	}
}
