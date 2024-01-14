package com.unitvectory.auth.datamodel.memory.model;

import com.unitvectory.auth.datamodel.model.ClientSummary;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class MemoryClientSummary implements ClientSummary {

	private String clientId;

	private String description;
}
