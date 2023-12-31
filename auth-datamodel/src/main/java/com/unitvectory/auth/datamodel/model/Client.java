package com.unitvectory.auth.datamodel.model;

import java.util.List;

import com.unitvectory.auth.util.HashingUtil;
import lombok.NonNull;

/**
 * Interface representing a client with its details and secrets.
 */
public interface Client {

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

    /**
     * Returns the salt used for hashing secrets.
     *
     * @return the salt.
     */
    String getSalt();

    /**
     * Returns the first client secret as hashed value; may be null if not set.
     *
     * @return the first client secret.
     */
    String getClientSecret1();

    /**
     * Returns the second client secret as hashed value; may be null if not set.
     *
     * @return the second client secret.
     */
    String getClientSecret2();

    /**
     * Returns a list of JWT Bearer details associated with the client.
	 * 
	 * These can be used in place of a secret for authentication.
     *
     * @return the list of JWT Bearer details.
     */
    List<ClientJwtBearer> getJwtBearer();

    /**
     * Hashes a secret using SHA-256 and the client's salt.
	 * 
	 * Used as a helper method when setting a client secret.
     *
     * @param secret the secret to hash.
     * @return the hashed secret.
     * @throws IllegalStateException if the salt is not set.
     */
    public default String hashSecret(@NonNull String secret) {
        if (this.getSalt() == null) {
            throw new IllegalStateException("Salt not set");
        }

        String hashedSecret = HashingUtil.sha256(secret);
        return HashingUtil.sha256(hashedSecret + this.getSalt());
    }

    /**
     * Verifies a secret against the stored client secrets.
     *
     * @param secret the secret to verify.
     * @return true if the secret matches either of the stored secrets, false otherwise.
     */
    public default boolean verifySecret(@NonNull String secret) {

        if (this.getSalt() == null) {
            return false;
        }

        // Hash the secret so it can be compared
        String hashedSecret = this.hashSecret(secret);

        if (this.getClientSecret1() != null && this.getClientSecret1().equals(hashedSecret)) {
            return true;
        }

        if (this.getClientSecret2() != null && this.getClientSecret2().equals(hashedSecret)) {
            return true;
        }

        return false;
    }
}
