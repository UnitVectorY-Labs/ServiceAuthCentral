# auth-server-token

This Spring Boot 3 application hosts the data plane of ServiceAuthCentral providing the two primary APIs, the JWKS end point and the token end point as well as the two secondary APIs for logging in.

This implementation utilizes a modular architecture for the underlying database implementing the `auth-datamodel` interfaces as well as the signing capabilities implementing the `auth-sign` interface. This means a specific implementation for these interfaces must be enabled at runtime which is accomplished through Spring Profiles.

External token verification is also modular implementing the `auth-verify` interface but the `auth-verify-auth0` implementation is enabled by default.

## Configuration

The following configuration attributes:

| Property                                | Required           | Description                                                                                      |
| --------------------------------------- | ------------------ | ------------------------------------------------------------------------------------------------ |
| sac.issuer                              | Yes                | The JWT issuer; populates the `iss` claim                                                        |
| sac.user.redirecturi                    | Yes                | The redirect URI for serviceauthcentralweb; multiple values can be specified separated by commas |
| sac.cors.origins                        | Yes                | CORS origins                                                                                     |
| sac.server.token.external.cache.seconds | No (default: 3600) | Number of seconds external JWKS is cached                                                        |
