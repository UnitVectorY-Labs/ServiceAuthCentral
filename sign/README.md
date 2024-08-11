# sign

This library provides the interfaces for listing and using signing keys for JWTs so that the underlying implementation can be swapped out as a runtime dependency.

## Repository Interfaces

Three different repositories are defined:

- `AuthorizationRepository`: Access and management of authorization records for which subjects are authorized to access which audiences.
- `ClientRepository`: Access and management of clients and their attributes for authentication.
- `JwkCacheRepository`: Caching interface for JWKs to provide persistence to external JWKS endpoints.

## Data Interfaces

The underlying data elements are handled through interfaces:

- `Authorization`: The authorization data
- `Client`: The client data
- `ClientJwtBearer`: The JWT metadata used to authenticate instead of client secrets
- `CachedJwk`: The cached JWK data
