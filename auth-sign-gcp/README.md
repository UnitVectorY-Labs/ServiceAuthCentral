# auth-sign-gcp

This library provides a [GCP KMS](https://cloud.google.com/security/products/security-key-management) implementation of the `auth-sign` interface.

The design here uses runtime dependency injection allowing for the database implementation to be replaced.

This implementation only supports the `RSA_SIGN_PKCS1_2048_SHA256` signing algorithm.
Therefore the GCP KMS key must have a purpose of `Asymmetric sign` and an algorithm of `2048 bit RSA - PKCS#1 v1.5 padding - SHA256 Digest`.

## Spring Boot Profile

Spring Boot 3's dependency injection is used to initialize the relevant Beans for interacting with Firestore. This is accomplished through profiles.

The `sign-gcp` profile is enabled to utilize GCP KMS.

## Configuration

The following configuration attributes:

| Property                                          | Required           | Description                      |
| ------------------------------------------------- | ------------------ | -------------------------------- |
| google.cloud.project                              | Yes                | GCP Project name                 |
| serviceauthcentral.sign.gcp.key.ring              | Yes                | KMS Key Ring Name                |
| serviceauthcentral.sign.gcp.key.location          | Yes                | KMS Key Ring Location            |
| serviceauthcentral.sign.gcp.key.name              | Yes                | KMS Key Name                     |
| serviceauthcentral.sign.gcp.cache.jwks.seconds    | No (default: 3600) | Length of time keys are cached   |
| serviceauthcentral.sign.gcp.cache.safety.multiple | No (default: 24)   | Multiple of cache before key use |

## Key Rotation and Caching Considerations

Public keys are retrieved from KMS and cached to avoid redundant API calls which may result in throttling.
The default amount of time for caching is 1 hour, but this can be configured using `serviceauthcentral.sign.gcp.cache.jwks.seconds`.

Due to this caching precautions must be taking when rotating keys.
KMS's built in ability to create and delete keys is utilized allowing multiple keys to be active at a time.
The problem arises when a new key is added, if it was immediately used for signing clients would not have the public key and would fail to verify it, therefore a period of time must elapse after it is created but before it can be used.

This period of time is configured using the `serviceauthcentral.sign.gcp.cache.safety.multiple` which has a default of 24 and is multiplied by `serviceauthcentral.sign.gcp.cache.jwks.seconds` whose default of 1 hour means that new keys will not be used for 1 day after they are created. The exception to this is when only 1 key is available, it will be use regardless of when it was created.

After this time elapses the key will be selected and used for signing and the older keys can be scheduled for deletion once any outstanding JWTs have also expired.

This procedure allows for key rotations without a production impact as long as clients are configured to not cache the JWKS response for longer than this configured
