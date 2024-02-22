# auth-verify-auth0

This library provides an implementation of the `auth-verify` interfaces using the Auth0 Java libraries.

## Spring Boot Profile

Spring Boot 3's dependency injection is used to initialize the relevant Beans. This is accomplished through profiles for the other modules but only one verify implementation is available so it is automatically enabled.
