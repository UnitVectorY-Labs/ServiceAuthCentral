# datamodel

This library provides the interfaces for accessing the repositories and data so that the underlying implementation can be swapped out as a runtime dependency.

## Repository Interfaces

The following interfaces are implemented to provide data persistence.

- `AuthorizationRepository`: Manage client authorizations; which clients are authorized to access others for centralized authorization model
- `ClientRepository`: Manage clients; catalog of all of the registered clients.
- `JwkCacheRepository`: Manage JWK cache; improves performance and availability for external authentications.
- `LoginCodeRepository`: Manage login codes; the authorization code repository to allow users to log in
- `LoginStateRepository`: Manage login state; the session needed to complete the OAuth user login flow
