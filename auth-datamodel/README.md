auth-datamodel
==============

This library provides the interfaces for accessing the repositories and data so that the underlying implementation can be swapped out as a runtime dependency.

Repository Interfaces
---------------------

Three different repositories are defined:

 - `SignService`: Service interface for signing operations and key management for JWTs and JWKS.

Data Interfaces
---------------

The underlying data elements are handled through interfaces:

 - `JsonWebKey`: Interface representing a JSON Web Key (JWK).
