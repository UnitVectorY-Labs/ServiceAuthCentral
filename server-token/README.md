# server-token

This Spring Boot 3 application hosts the data plane of ServiceAuthCentral providing the two primary APIs, the JWKS end point and the token end point as well as the two secondary APIs for logging in.

This implementation utilizes a modular architecture for the underlying database implementing the `datamodel` interfaces as well as the signing capabilities implementing the `sign` interface. This means a specific implementation for these interfaces must be enabled at runtime which is accomplished through Spring Profiles.

External token verification is also modular implementing the `verify` interface but the `verify-auth0` implementation is enabled by default.

## Configuration

The following configuration attributes:

| Property                                | Required           | Description                                                                                      |
| --------------------------------------- | ------------------ | ------------------------------------------------------------------------------------------------ |
| sac.issuer                              | Yes                | The JWT issuer; populates the `iss` claim                                                        |
| sac.user.redirecturi                    | Yes                | The redirect URI for serviceauthcentralweb; multiple values can be specified separated by commas |
| sac.cors.origins                        | Yes                | CORS origins                                                                                     |
| sac.token.url                           | Yes                | The base domain name of the token server (without the trailing slash)                            |
| sac.server.token.external.cache.seconds | No (default: 3600) | Number of seconds external JWKS is cached                                                        |

Different providers can be configured to enable the ability to log in with different account types.

**GitHub:**

| Property                              | Required | Description                                 |
| ------------------------------------- | -------- | ------------------------------------------- |
| sac.user.provider.github.clientid     | Yes      | The clientId for the GitHub application     |
| sac.user.provider.github.clientsecret | Yes      | The clientSecret for the GitHub application |


**Google:**

| Property                              | Required | Description                                 |
| ------------------------------------- | -------- | ------------------------------------------- |
| sac.user.provider.google.clientid     | Yes      | The clientId for the Google application     |
| sac.user.provider.google.clientsecret | Yes      | The clientSecret for the Google application |
