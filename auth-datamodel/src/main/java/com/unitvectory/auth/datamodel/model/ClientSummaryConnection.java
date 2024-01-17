package com.unitvectory.auth.datamodel.model;

import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClientSummaryConnection {

	private final List<ClientSummaryEdge> edges;

	private final PageInfo pageInfo;
}
