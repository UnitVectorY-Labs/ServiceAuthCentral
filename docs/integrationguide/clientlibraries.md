# Client Libraries

ServiceAuthCentral provides an implementation that is compatible with the OAuth 2.0 protocol.  Therefore existing code libraries may be compatible with the ServiceAuthCentral token service.  This is especially true for resource servers that are validating JWTs with the JWKS endpoint where standard JWT libraries can be easily used.

For clients requesting tokens from the ServiceAuthCentral token service, the client libraries will need to be able to make HTTP requests to the token service. To simplify this process, ServiceAuthCentral provides a client library that can be used to request tokens from the token service.

## ServiceAuthCentral Java Client Library

The client library [serviceauthcentral-client-java](https://github.com/UnitVectorY-Labs/serviceauthcentral-client-java) provides a streamlined way to request tokens from the ServiceAuthCentral token service.  The client library is designed to be used by client services that need to request tokens from the token service. It heavily leverages the builder design pattern to make it easy to construct the client and request tokens.

## JWT Bearer Token Flow Example (GCP Service Account)

The preferred method for requesting tokens from the ServiceAuthCentral token service is to use the JWT bearer token flow.  This flow utilizes GCP's service accounts to allow a workload running on GCP to use the JWT to authenticate to ServiceAuthCentral.  This flow is preferred as it does not require a client secret to be stored in the client service.

When using `serviceauthcentral-client-java` the use of GCP with `GCPJwtCredentialsProvider` requires the optional dependency `google-auth-library-oauth2-http` to be included in the project.  This dependency is used to authenticate to GCP to request the JWT components to authenticate to ServiceAuthCentral.

```xml
<dependency>
    <groupId>com.google.auth</groupId>
    <artifactId>google-auth-library-oauth2-http</artifactId>
    <version>LATEST_VERSION</version>
</dependency>
```

This example demonstrates the use of the decorator pattern to add caching to the client library. The `CachingSACClientDecorator` is used to cache the ServiceAuthCentral tokens to avoid unnecessary token requests and to handle token expiration automatically. The `GCPJwtCredentialsProvider` is used to provide the necessary JWT components to authenticate to ServiceAuthCentral, additional caching is not needed with this provider as it is already cached by the GCP libraries.

```java
package example;

import com.google.auth.oauth2.GoogleCredentials;
import com.unitvectory.serviceauthcentral.client.CachingSACClientDecorator;
import com.unitvectory.serviceauthcentral.client.GCPJwtCredentialsProvider;
import com.unitvectory.serviceauthcentral.client.SACClient;
import com.unitvectory.serviceauthcentral.client.SACClientDefault;
import com.unitvectory.serviceauthcentral.client.TokenRequest;
import com.unitvectory.serviceauthcentral.client.TokenResponse;


public class ClientExample {

    public static void main(String[] args) throws Exception {

        String issuer = ""; // The issuer URL for ServiceAuthCentral
        String clientId = ""; // The client ID of the client service
        String jwtBearerAudience = ""; // The audience for the JWT bearer token

        SACClient client = CachingSACClientDecorator.builder().client(SACClientDefault.builder()
                .issuer(issuer)
                .credentialsProvider(GCPJwtCredentialsProvider.builder()
                        .googleCredentials(credentials)
                        .clientId(clientId)
                        .targetAudience(jwtBearerAudience)
                        .build())
                .build()).build();

        // Request a token for the target service.
        // Client must be authorized to access this audience.
        String targetAudience = ""; // The target audience for the token
        TokenResponse response = client.getToken(TokenRequest.builder().audience(
                targetAudience)
                .build());

        // The access token can now be used to authenticate with the target service.
        String accessToken = response.getAccessToken();
        System.out.println(accessToken);
    }
}
```

The JWT Bearer must be configured in ServiceAuthCentral as a method to authorize a specific client, this requires specifying the following configuration values:

- **JWKS URL:** The URL to the JWKS endpoint ServiceAuthCentral will use to validate the JWT.
- **Issuer:** The issuer of the JWT in the JWT Bearer token. This will be matched with the JWKS and the `iss` claim in the JWT.
- **Subject:** The subject of the JWT in the JWT Bearer token. This is how the client is identified in the `sub` claim in the JWT.
- **Audience:** The audience of the JWT in the JWT Bearer token. This is flexible, but a good choice is to match the client ID within ServiceAuthCentral as that is the audience the token will be used to access.  This is the `aud` claim in the JWT.

In the case that a GCP Service Account is used on GCP the token is requested by Google and signed  the configuration would look like:

- **JWKS URL:** `https://www.googleapis.com/oauth2/v3/certs`
- **Issuer:** `https://accounts.google.com`
- **Subject:** `{GOOGLE_SERVICE_ACCOUNT_CLIENT_ID}`
- **Audience:** `{SERVICEAUTHCENTRAL_CLIENT_ID}`

In this case the issuer is Google and not the service account itself.  The subject of `{GOOGLE_SERVICE_ACCOUNT_CLIENT_ID}` is the client Id of the service account that takes for form of a numeric string like "000000000000000000000".

## Local JWT Bearer Token Example (GCP Service Account JSON)

A JWT bearer token can be signed locally if a client has the private key.  This is not the preferred method as it requires the client to have the private key and the client secret. However, it is possible to use this method with a GCP service account given the JSON file. This is enabled by the fact Google provides a public JWKS endpoint for service accounts.

This example demonstrates how to use the client library to request a token using the JWT bearer token flow and demonstrates how the decorator pattern can be optionally used to add caching to the client library. The `CachingSACClientDecorator` is used to cache the ServiceAuthCentral tokens to avoid unnecessary token requests and to handle token expiration automatically. Additionally the `CachingCredentialsProviderDecorator` is used to cache the construction of the local JWTs locally to avoid unnecessary construction of the JWTs.

```java
package example;

import java.io.File;

import com.unitvectory.serviceauthcentral.client.CachingCredentialsProviderDecorator;
import com.unitvectory.serviceauthcentral.client.CachingSACClientDecorator;
import com.unitvectory.serviceauthcentral.client.LocalJwtCredentialsProvider;
import com.unitvectory.serviceauthcentral.client.SACClient;
import com.unitvectory.serviceauthcentral.client.SACClientDefault;
import com.unitvectory.serviceauthcentral.client.TokenRequest;
import com.unitvectory.serviceauthcentral.client.TokenResponse;

public class ClientExample {

    public static void main(String[] args) {

        String issuer = ""; // The issuer URL for ServiceAuthCentral
        String clientId = ""; // The client ID of the client service
        String gcpServiceAccountJsonPath = ""; // The path to the GCP service account JSON file
        String jwtBearerAudience = ""; // The audience for the JWT bearer token

        SACClient client = CachingSACClientDecorator.builder().client(SACClientDefault.builder()
                .issuer(issuer)
                .credentialsProvider(CachingCredentialsProviderDecorator.builder()
                        .provider(LocalJwtCredentialsProvider.loadGCPServiceAccountFile(
                                new File(gcpServiceAccountJsonPath))
                                .clientId(clientId)
                                .audience(jwtBearerAudience)
                                .build())
                        .build())
                .build()).build();

        // Request a token for the target service.
        // Client must be authorized to access this audience.
        String targetAudience = ""; // The target audience for the token
        TokenResponse response = client.getToken(TokenRequest.builder().audience(
                targetAudience)
                .build());

        // The access token can now be used to authenticate with the target service.
        String accessToken = response.getAccessToken();
    }
}
```

The above example utilizes a helper method for specifically loading in the JSON file, but the `LocalJwtCredentialsProvider` can be used directly to load the necessary JWT components to sign a token to be used to request a token from the ServiceAuthCentral token service.

```java
LocalJwtCredentialsProvider.builder()
        .issuer(jwtBearerIssuer)
        .keyId(jwtBearerKeyId)
        .clientId(jwtBearerClientId)
        .audience(jwtBearerAudience)
        .privateKeyPem(jwtBearerPrivateKey)
        .build();
```

In the case that a GCP Service Account is directly used for this example with a GCP Service Account the configuration would look like:

- **JWKS URL:** `https://www.googleapis.com/service_accounts/v1/jwk/{SERVICE_ACCOUNT}@{PROJECT}.iam.gserviceaccount.com`
- **Issuer:** `{SERVICE_ACCOUNT}@{PROJECT}.iam.gserviceaccount.com`
- **Subject:** `{SERVICE_ACCOUNT}@{PROJECT}.iam.gserviceaccount.com`
- **Audience:** `{SERVICEAUTHCENTRAL_CLIENT_ID}`

Since the private key is used to identify the service account directly the issuer and subject are identical.  The JWKS url is the endpoint that Google provides to get the public key for the service account. As mentioned earlier, the audience claim should match the client ID in ServiceAuthCentral that the token will be used to access.

## Client Credentials Example

While discouraged, the client credentials flow can be used to request a token from the ServiceAuthCentral token service.  This flow requires the client ID and client secret to be provided in the request. ServiceAuthCentral's philosophy is to avoid storing client secrets in client services, so this flow is discouraged. However, it is possible to use this flow with the client library.

This example demonstrates how to use the client library to request a token using the client credentials flow and demonstrates how the decorator pattern can be optionally used to add caching to the client library.

```java
package example;

import com.unitvectory.serviceauthcentral.client.CachingCredentialsProviderDecorator;
import com.unitvectory.serviceauthcentral.client.CachingSACClientDecorator;
import com.unitvectory.serviceauthcentral.client.SACClient;
import com.unitvectory.serviceauthcentral.client.SACClientDefault;
import com.unitvectory.serviceauthcentral.client.StaticClientCredentialsProvider;
import com.unitvectory.serviceauthcentral.client.TokenRequest;
import com.unitvectory.serviceauthcentral.client.TokenResponse;

public class ClientExample {

    public static void main(String[] args) {

        String issuer = ""; // The issuer URL for ServiceAuthCentral
        String clientId = ""; // The client ID of the client service
        String clientSecret = ""; // The client secret of the client service

        // Construct the SAC client, include caching to avoid unnecessary token
        // requests and to handle token expiration automatically.
        SACClient client = CachingSACClientDecorator.builder().client(SACClientDefault.builder()
                .issuer(issuer)
                .credentialsProvider(CachingCredentialsProviderDecorator.builder()
                        .provider(StaticClientCredentialsProvider.builder()
                                .clientId(clientId)
                                .clientSecret(clientSecret)
                                .build())
                        .build())
                .build()).build();

        // Request a token for the target service.
        // Client must be authorized to access this audience.
        String targetAudience = ""; // The target audience for the token
        TokenResponse response = client.getToken(TokenRequest.builder().audience(
                targetAudience)
                .build());

        // The access token can now be used to authenticate with the target service.
        String accessToken = response.getAccessToken();
    }
}
```
