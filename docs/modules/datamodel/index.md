# Data Model

The data model module provides the interfaces for accessing the repositories and data so that the underlying implementation can be swapped out as a runtime dependency.

## Repository Interfaces

The following interfaces are implemented to provide data persistence.

- `AuthorizationRepository`: Manage client authorizations; which clients are authorized to access others for centralized authorization model
- `ClientRepository`: Manage clients; catalog of all of the registered clients.
- `JwkCacheRepository`: Manage JWK cache; improves performance and availability for external authentications.
- `LoginCodeRepository`: Manage login codes; the authorization code repository to allow users to log in
- `LoginStateRepository`: Manage login state; the session needed to complete the OAuth user login flow

A flexible deployment is supported by allowing different underlying database technologies based on the desired deployment mechanism.

## Repository Implementations

There are multiple data model implementations that are available. Exactly one module must be enabled at runtime.

!!! note
    Each module implementation will have additional properties that are required to be set for it to work correctly when it is enabled, typically through envirionment variables.

- [Data Model - Firestore](./firestore.md): Firestore implementation for the repository interfaces
- [Data Model - Memory](./memory.md): In-memory implementation for the repository interfaces used for testing and development
- [Data Model - Postgres](./postgres.md): PostgreSQL implementation for the repository interfaces
