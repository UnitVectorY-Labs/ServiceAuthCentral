package com.unitvectory.auth.datamodel.model;

/**
 * Interface for a client summary.
 */
public interface ClientSummary {

	/**
	 * Returns the unique identifier for the client.
	 *
	 * @return the client ID.
	 */
	String getClientId();

	/**
	 * Returns a description of the client.
	 *
	 * @return the client description.
	 */
	String getDescription();
}
