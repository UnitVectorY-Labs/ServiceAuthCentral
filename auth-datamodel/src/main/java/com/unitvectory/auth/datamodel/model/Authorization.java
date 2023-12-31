package com.unitvectory.auth.datamodel.model;

/**
 * Authorization document interface
 */
public interface Authorization {

	/**
	 * the document id is intended to be a combination of the subject and audience
	 * 
	 * @return the document id uniquely identifying this document
	 */
	String getDocumentId();

	/**
	 * the client that is authorized as the subject for a token exchange
	 * 
	 * @return the clientId for the subject
	 */
	String getSubject();

	/**
	 * the audience that is authorized as the audience for a token exchange
	 * 
	 * @return the clientId for the audience
	 */
	String getAudience();
}
