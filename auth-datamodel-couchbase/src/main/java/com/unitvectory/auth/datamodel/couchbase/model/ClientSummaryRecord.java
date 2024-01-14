package com.unitvectory.auth.datamodel.couchbase.model;

import com.unitvectory.auth.datamodel.model.ClientSummary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientSummaryRecord implements ClientSummary {

	private String clientId;

	private String description;
}
