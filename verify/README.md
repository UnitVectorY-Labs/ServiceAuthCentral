# verify

This library provides the interfaces for validating the signatures of JWTs in addition to retrieving the JWK from a remote server.

## Service Interfaces

Two different interfaces are defined:

- `JwksResolver`: Retrieve remote JWKs from another server.
- `JwtVerifier`: Extract claims from JWT and verify JWT signature.
