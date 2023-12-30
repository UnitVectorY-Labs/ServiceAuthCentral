package com.unitvectory.auth.datamodel.memory.repository;

import java.util.HashMap;
import java.util.Map;

import com.unitvectory.auth.datamodel.exception.NotFoundException;
import com.unitvectory.auth.datamodel.memory.model.MemoryClient;
import com.unitvectory.auth.datamodel.model.Client;
import com.unitvectory.auth.datamodel.repository.ClientRepository;

import lombok.NonNull;

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
	public void putClient(@NonNull String clientId, String description, String salt) {
		MemoryClient record = this.memory.get(clientId);
		if (record == null) {
			record = MemoryClient.builder().clientId(clientId).description(description).salt(salt).build();
			this.memory.put(clientId, record);
		} else {
			record.setDescription(description);
			record.setSalt(salt);
		}
	}

	@Override
	public void saveClientSecret1(@NonNull String clientId, @NonNull String hashedSecret) {
		MemoryClient record = this.memory.get(clientId);
		if (record == null) {
			throw new NotFoundException("client not found");
		}

		record.setClientSecret1(hashedSecret);
	}

	@Override
	public void saveClientSecret2(@NonNull String clientId, @NonNull String hashedSecret) {
		MemoryClient record = this.memory.get(clientId);
		if (record == null) {
			throw new NotFoundException("client not found");
		}

		record.setClientSecret2(hashedSecret);
	}

	@Override
	public void clearClientSecret1(@NonNull String clientId) {
		MemoryClient record = this.memory.get(clientId);
		if (record == null) {
			throw new NotFoundException("client not found");
		}

		record.setClientSecret1(null);
	}

	@Override
	public void clearClientSecret2(@NonNull String clientId) {
		MemoryClient record = this.memory.get(clientId);
		if (record == null) {
			throw new NotFoundException("client not found");
		}

		record.setClientSecret2(null);
	}

}
