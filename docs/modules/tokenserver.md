---
layout: default
title: Token Server
parent: Integration Guide
nav_order: 1
---

# Token Server

The token server provides the data plane for ServiceAuthCentral in the form of the token endpoint used to vend the JWT access tokens.

The token server provides two major functions. First is providing the JWKS endpoint to resource servers. The public keys are retrieved from the specific implementation of the `sign` module. And the primary function is providing the OAuth 2.0 endpoint for clients to request access tokens which are returned in the form of JWTs.

```mermaid
flowchart LR
    subgraph SAC[ServiceAuthCentral]
        TokenServer[Token Server]

        subgraph Sign
            PrivateKey[Private Key]
            PublicKey[Public Key]
        end

        subgraph DataModel[Data Model]
            authorizations[(authorizations)]
            clients[(clients)]
        end

        TokenServer -- Get Authorization--> authorizations
        TokenServer -- Get Client--> clients

        TokenServer -- Get Public Key--> PublicKey
        TokenServer -- Sign JWT --> PrivateKey
    end

    ResourceServer[Resource Server]
    ResourceServer -- GET JWKS --> TokenServer

    Client -- Request Token --> TokenServer

    Client -. API Call.-> ResourceServer
```

## Build Token Server Docker Image

Both the manage and token service are compiled from the same repository git repository and Dockerfile. The command to build the token server requires the correct build-arg to be passed in to generate the docker container for the token server components.

```
docker build -f Dockerfile --build-arg MAVEN_PROFILE=server-token -t serviceauthcentral/server-token .
```

## Run Token Server Using Docker

The Docker image for the token server is configured using environment variables.

> {: .important }
> These environment variables are required to be identical between the manage and token server deployment.

| Environment Variable   | Required | Description                                                                                                                                                                                                            |
| ---------------------- | -------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| SPRING_PROFILES_ACTIVE | Yes      | A corresponding profile must be selected for each of [Data Model]({{ site.baseurl }}{% link modules/datamodel.md %}) and [Sign]({{ site.baseurl }}{% link modules/sign.md %}). These are set as a comma separated list. |
| SAC_USER_REDIRECTURI   | Yes      | The redirect URI for serviceauthcentralweb; multiple values can be specified separated by commas                                                                                                                       |
| SAC_ISSUER             | Yes      | The JWT issuer url. Typically the domain name of the token server.                                                                                                                                                     |
| SAC_CORS_ORIGINS       | Yes      | CORS origins for web based logins. This is the domain name ServiceAuthCentral Web is deployed to.                                                                                                                      |
