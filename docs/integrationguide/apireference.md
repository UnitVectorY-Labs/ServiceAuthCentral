---
layout: default
title: API Reference
parent: Integration Guide
nav_order: 3
---

# API Reference

The following is an API reference for the ServiceAuthCentral token service and its use by `Client Services` and `Resource Services`. This is how your services will interact with the ServiceAuthCentral token service to request and validate access tokens.

> {: .important }
> For the API reference on the manage server and how to manage clients and authorizations see the [Contributor Guide - API Reference]({{ site.baseurl }}{% link contributorguide/apireference.md %}) page.

## POST /v1/token

The `POST /v1/token` endpoint on the token server is the OAuth 2.0 token endpoint using for requesting access tokens that take the form of a JWT. The endpoint is used by `Client Services` to request access tokens for `Resource Services`.  The two flows supported are client credentials and the preferred jwt bearer flow.


### Client Credentials Flow

Use this flow when you have a client secret (not preferred) to request an access token for your desired audience.

```bash
curl -X POST "https://token.example.com/v1/token" \
-H "Content-Type: application/x-www-form-urlencoded" \
--data-urlencode "grant_type=client_credentials" \
--data-urlencode "client_id=your_client_id" \
--data-urlencode "client_secret=your_client_secret" \
--data-urlencode "audience=audience_to_access_client_id"
```

### JWT Bearer Token Flow

Use this flow when you want to authenticate using a JWT from another service, such as a GCP service account, without a client secret. The audience parameter is still required.

```bash
curl -X POST "https://token.example.com/v1/token" \
-H "Content-Type: application/x-www-form-urlencoded" \
--data-urlencode "grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer" \
--data-urlencode "assertion=eyJhbGF...7EEaTA" \
--data-urlencode "client_id=your_client_id" \
--data-urlencode "audience=audience_to_access_client_id"
```

In both requests, the audience parameter is crucial as it specifies which service the token should grant access to. This parameter is checked against the authorization policies configured in ServiceAuthCentral to ensure the requesting client is authorized to access the specified service.

### Response

The response follows the standard OAuth 2.0 response format with the token being returned as a JWT.

```json
{
  "access_token": "eyJhbGF...7EEaTA",
  "token_type": "Bearer",
  "expires_in": 3600
}
```

## GET /.well-known/jwks.json

The `GET /.well-known/jwks.json` endpoint on the token server is used by `Resource Services` to obtain the public key used to validate the access tokens. The public key is used to verify the signature of the access token. This follows the OAuth 2.0 [JSON Web Key (JWK)](https://datatracker.ietf.org/doc/html/rfc7517) standard.

## GET /.well-known/openid-configuration

The `GET /.well-known/openid-configuration` endpoint on the token server is used by `Resource Services` to obtain the configuration information for the token server. This includes the token endpoint, the public key endpoint, and other configuration information.
