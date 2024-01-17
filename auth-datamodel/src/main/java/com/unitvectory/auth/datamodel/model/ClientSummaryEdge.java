package com.unitvectory.auth.datamodel.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClientSummaryEdge {

	private final ClientSummary node;

	private final String cursor;
}
