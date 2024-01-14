package com.unitvectory.auth.datamodel.gcp.model;

import com.google.cloud.firestore.annotation.IgnoreExtraProperties;
import com.unitvectory.auth.datamodel.model.ClientSummary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IgnoreExtraProperties
public class ClientSummaryRecord implements ClientSummary {

	private String clientId;

	private String description;
}
