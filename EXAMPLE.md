# Example

The following provides an example configuration for deploying this application.

## GCP Hosting

This example uses GCP to host the container providing access to

**Token Server**

The token server can be deployed as a Cloud Run service.
It can utilize GCP Firesture through the `auth-datamodel-firestore` module with the `datamodel-firestore` Spring Boot profile.
It can utilize GCP KMS through the `auth-sign-gcp` module with the `sign-gcp` Spring Boot profile.

It requires the following environment variables.

| Environment Variable                  | Example Value                      |
| ------------------------------------- | ---------------------------------- |
| GOOGLE_CLOUD_PROJECT                  | Example                            |
| SPRING_PROFILES_ACTIVE                | datamodel-firestore,sign-gcp       |
| SAC_SIGN_GCP_KEY_RING                 | authorization                      |
| SAC_SIGN_GCP_KEY_NAME                 | jwk-key                            |
| SAC_SERVER_TOKEN_ISSUER               | https://token.example.com          |
| SAC_USER_PROVIDER_GITHUB_CLIENTID     | github-client-id                   |
| SAC_USER_PROVIDER_GITHUB_CLIENTSECRET | github-client-secret               |
| SAC_USER_REDIRECTURI                  | https://admin.example.com/callback |
| SAC_CORS_ORIGINS                      | https://admin.example.com          |

**Manage Server**

The manage server can be deployed as a Cloud Run service.
It must utilize the same GCP Firestore collections utilizing the the `auth-datamodel-firestore` module with the `datamodel-firestore` Spring Boot profile.

It requires the following environment variables.

| Environment Variable   | Example Value             |
| ---------------------- | ------------------------- |
| GOOGLE_CLOUD_PROJECT   | Example                   |
| SPRING_PROFILES_ACTIVE | datamodel-firestore       |
| SAC_ISSUER             | https://token.example.com |
| SAC_CORS_ORIGINS       | https://admin.example.com |
