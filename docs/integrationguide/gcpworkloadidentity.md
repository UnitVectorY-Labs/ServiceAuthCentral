---
layout: default
title: GCP Workload Identity
parent: Integration Guide
nav_order: 4
---

# GCP Workload Identity

One of the benefits of using Google Cloud Platform (GCP) is the ability to use [Workload Identity Federation](https://cloud.google.com/iam/docs/workload-identity-federation) to securely access Google Cloud services with external credentials through an OIDC-compliant token service.  This allows you to use the ServiceAuthCentral token service to request access tokens for Google Cloud services using a Google Service Account.

With ServiceAuthCentral acting as an authorization server requiring each audience to be authorized, this results in each client needing to be authorized to access GCP generally for the Workload Identity Pool.  Then the corresponding GCP project can authoriz

## Requesting Token

To request a token, make a `POST` request to `https://sts.googleapis.com/v1/token` with the following parameters in the `application/x-www-form-urlencoded` format:

```
subjectToken={SERVICEAUTHCENTRAL_JWT}&
audience=//iam.googleapis.com/projects/{project_id}/locations/global/workloadIdentityPools/{POOL_ID}/providers/{PROVIDER_ID}&
grantType=urn:ietf:params:oauth:grant-type:token-exchange&
requestedTokenType=urn:ietf:params:oauth:token-type:access_token&
scope=https://www.googleapis.com/auth/cloud-platform&
subjectTokenType=urn:ietf:params:oauth:token-type:jwt
```
```