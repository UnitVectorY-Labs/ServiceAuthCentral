# Verify

The verify module provides the interfaces for verifying / validating the JWTs so that the underlying implementation can be swapped out as a runtime dependency.

## Verify Interfaces

The following interfaces are implemented to provide JWT and JWKS validation.

- `JwksResolver`: Retrieve remote JWKs from another server.
- `JwtVerifier`: Extract claims from JWT and verify JWT signature.

## Verify Implementations

There is currently only one implementation for verify, but this is left as an integration point for future implementations. As such it is enabled by default.

- [Verify - Auth0](./auth0.md): A verify implementation that utilizes Auth0's Java library (this doesn't actually interact with Auth0)
