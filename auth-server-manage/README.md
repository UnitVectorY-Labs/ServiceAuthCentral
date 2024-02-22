# auth-server-manage

This Spring Boot 3 application hosts the control plane of ServiceAuthCentral providing the GraphQL interface.

This implementation utilizes a modular architecture for the underlying database implementing the `auth-datamodel` interfaces. This means a specific implementation for these interfaces must be enabled at runtime which is accomplished through Spring Profiles.

## Configuration

The following configuration attributes:

| Property         | Required | Description        |
| ---------------- | -------- | ------------------ |
| sac.issuer       | Yes      | The JWT issuer url |
| sac.cors.origins | Yes      | CORS origins       |

## Authentication and Authorization

While the primary purpose of ServiceAuthCentral is server-to-server authentication with OAuth 2.0, this management API needs a mechanism to vend access tokens to the web based front end specifically [serviceauthcentralweb](https://github.com/UnitVectorY-Labs/serviceauthcentralweb). To support this auth-server-token supports a PKCE login flow.

The tokens that are able to call the management API have the audience value which has the value of the issuer. Meaning in the JWT the issuer and audience have the same value.
