---
layout: default
title: Sign - GCP
parent: Integration Guide
nav_order: 6
---

# Sign - GCP


This module provides an implementation for the JWT signing using asymmetric keys for signing using stored in [GCP KMS](https://cloud.google.com/security/products/security-key-management) with the benefit being the private key is not accessable thereby providing additional protections.

```mermaid
flowchart LR
    subgraph gcpproject[GCP Project]
        ServiceAuthCentral

        subgraph KMS
            PrivateKey[Private Key]
            PublicKey[Public Key]
        end

        DataModel[(Data Model)]

        ServiceAuthCentral -- Get Client/Authorization--> DataModel

        ServiceAuthCentral -- Get Public Key--> PublicKey
        ServiceAuthCentral -- Sign --> PrivateKey
    end

    ResourceServer[Resource Server]
    ResourceServer -- JWKS --> ServiceAuthCentral

    Client -- Request Token --> ServiceAuthCentral
```

# Configuration

This module is enabled by setting the `SPRING_PROFILES_ACTIVE` to include the profile `sign-gcp`.

The following environment variables are used by the GCP KMS module:

| Environment Variable               | Required             | Description                      |
| ---------------------------------- | -------------------- | -------------------------------- |
| GOOGLE_CLOUD_PROJECT               | Yes                  | GCP Project name                 |
| SAC_SIGN_GCP_KEY_RING              | Yes                  | KMS Key Ring Name                |
| SAC_SIGN_GCP_KEY_NAME              | Yes                  | KMS Key Name                     |
| SAC_SIGN_GCP_KEY_LOCATION          | No (default: global) | KMS Key Ring Location            |
| SAC_SIGN_GCP_CACHE_JWKS_SECONDS    | No (default: 3600)   | Length of time keys are cached   |
| SAC_SIGN_GCP_CACHE_SAFETY_MULTIPLE | No (default: 24)     | Multiple of cache before key use |
