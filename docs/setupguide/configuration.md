# Configuration

The design principal behind ServiceAuthCentral is to be modular therefore there is some complexity to the configuration.  The guide here will walk through the minimal configuration needed to configure ServiceAuthCentral for running on GCP.

## Token API Configuration

The [token server](../modules/tokenserver.md) requires the following environment variables to be set as a minimum viable deployment:

| Environment Variable   | Description                                                      | Example                            |
| ---------------------- | ---------------------------------------------------------------- | ---------------------------------- |
| GOOGLE_CLOUD_PROJECT   | GCP Project Name                                                 | `my-project-name`                  |
| SPRING_PROFILES_ACTIVE | Used to enable the modules                                       | `datamodel-firestore,sign-gcp`     |
| SAC_ISSUER             | The issuer URL used to identify the server                       | `https://token.example.com`        |
| SAC_CORS_ORIGINS       | The comma separated list of Admin URLs to enable CORS            | `https://admin.example.com`        |
| SAC_USER_REDIRECTURI   | The comma separated list of redirector URLs for the admin portal | https://admin.example.com/callback |

The [data model Firestore module](../modules/datamodel/firestore.md) requires the following environment variables to be set:

| Environment Variable   | Description      | Example           |
| ---------------------- | ---------------- | ----------------- |
| GOOGLE_CLOUD_PROJECT   | GCP Project Name | `my-project-name` |

This assumes the default collection names are used.

The [sign GCP module](../modules/sign/gcp.md) requires the following environment variables to be set:

| Environment Variable   | Description       | Example                       |
| ---------------------- | ----------------- | ----------------------------- |
| GOOGLE_CLOUD_PROJECT   | GCP Project Nam e | `my-project-name`             |
| SAC_SIGN_GCP_KEY_RING  | KMS Key Ring Name | `serviceauthcentral-key-ring` |
| SAC_SIGN_GCP_KEY_NAME  | KMS Key Name      | `serviceauthcentral-sign-key` |

This assumes the keyring and key are in the global location.

The admin console requires configing ann external OAuth provider to authenticate users.

The [user GitHub module](../modules/user/github.md) requires the following environment variables to be set:

| Environment Variable                  | Description                         | Example              |
| ------------------------------------- | ----------------------------------- | -------------------- |
| SAC_USER_PROVIDER_GITHUB_CLIENTID     | The clientId provided by GitHub     | github-client-id     |
| SAC_USER_PROVIDER_GITHUB_CLIENTSECRET | The clientSecret provided by GitHub | github-client-secret |

The [user Google module](../modules/user/google.md) requires the following environment variables to be set:

| Environment Variable                  | Description                                                                                            | Example                   |
| ------------------------------------- | ------------------------------------------------------------------------------------------------------ | ------------------------- |
| SAC_USER_PROVIDER_GOOGLE_CLIENTID     | The clientId provided by Google                                                                        | google-client-id          |
| SAC_USER_PROVIDER_GOOGLE_CLIENTSECRET | The clientSecret provided by Google                                                                    | google-client-secret      |
| SAC_TOKEN_URL                         | The base URL for the token server which will end with "/login/callback" needed for Google's OAuth flow | https://token.example.com |

## Manage API Configuration

The [manage server](../modules/manageserver.md) requires the following environment variables to be set as a minimum viable deployment:

| Environment Variable   | Description                                            | Example                     |
| ---------------------- | ------------------------------------------------------ | --------------------------- |
| SPRING_PROFILES_ACTIVE | Used to enable the modules                             | `datamodel-firestore`       |
| SAC_ISSUER             | The issuer URL used to identify the server             | `https://token.example.com` |
| SAC_CORS_ORIGINS       | The comma separated list of Admin URLs to enable CORES | `https://admin.example.com` |

The [data model Firestore module](../modules/datamodel/firestore.md) requires the following environment variables to be set:

| Environment Variable   | Description      | Example           |
| ---------------------- | ---------------- | ----------------- |
| GOOGLE_CLOUD_PROJECT   | GCP Project Name | `my-project-name` |

## Web Configuration

The [serviceauthcentralweb](https://github.com/UnitVectorY-Labs/serviceauthcentralweb) requires the following variables to be configured for build and deploying:

| Environment Variable                | Description                                                      |
| ----------------------------------- | ---------------------------------------------------------------- |
| VUE_APP_SAC_MANAGE_URI              | The "/graphql" URL for ServiceAuthCentral's manage server        |
| VUE_APP_SAC_REDIRECT_URI            | The "/callback" URL for serviceauthcentralweb                    |
| VUE_APP_SAC_AUTHORIAZATION_ENDPOINT | The "/login/authorize" URL for ServiceAuthCentral's token server |
| VUE_APP_SAC_TOKEN_ENDPOINT          | The "/v1/token" URL for ServiceAuthCentral's token server        |
| VUE_APP_SAC_ISSUER                  | The issuer configured for ServiceAuthCentral                     |
