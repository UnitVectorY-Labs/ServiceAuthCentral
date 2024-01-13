package com.unitvectory.auth.datamodel.couchbase.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.unitvectory.auth.datamodel.model.Client;
import com.unitvectory.auth.datamodel.model.ClientJwtBearer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRecord implements Client {

	private String documentId;

	private String clientId;

	private String description;

	private String salt;

	private String clientSecret1;

	private String clientSecret2;

	private List<ClientJwtBearerRecord> jwtBearer;

	@Override
	public List<ClientJwtBearer> getJwtBearer() {
		if (this.jwtBearer == null) {
			return Collections.emptyList();
		}

		return jwtBearer.stream().map(obj -> (ClientJwtBearer) obj)
				.collect(Collectors.toCollection(ArrayList::new));
	}
}
