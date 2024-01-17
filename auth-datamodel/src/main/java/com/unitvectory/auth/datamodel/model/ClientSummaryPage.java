package com.unitvectory.auth.datamodel.model;

import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClientSummaryPage {

	/**
	 * Gets the cursor for the page.
	 * 
	 * @return the cursor
	 */
	private final PageInfo pageInfo;

	/**
	 * Gets the list of client summary records
	 * 
	 * @return the list of client summary records
	 */
	private final List<ClientSummary> clients;
}
