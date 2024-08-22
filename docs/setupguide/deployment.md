---
layout: default
title: Deployment
parent: Setup Guide
nav_order: 1
---

# Deployment

Deploying ServiceAuthCentral is intended to be flexible having been built in a modular way. The following are the steps to deploy ServiceAuthCentral in the context of a GCP envirionment:

## Prerequisites

ServiceAuthCentral has minimal external dependencies.  While it is possible for different [data model]({{ site.baseurl }}{% link modules/datamodel.md %}) implementations to be used, the default implementation uses [Google Firestore]({{ site.baseurl }}{% link modules/datamodelfirestore.md %}).

The Firestore database can be deployed using the OpenTofu module [serviceauthcentral-firestore-gcp-tofu](https://github.com/UnitVectorY-Labs/serviceauthcentral-firestore-gcp-tofu) which will deploy the Firestore database itself, along with the necessary indexes and TTL configuration.  For multi-region deployments refer to the  [high availability]({{ site.baseurl }}{% link setupguide/highavailability.md %}) guide.

**Manual deployment of Firestore database with `gcloud`**

```bash
# This example creates a Firestore database named 'serviceauthcentral' in the us-east4 region

# Create Firestore database (if needed)
gcloud firestore databases create \
  --database="serviceauthcentral" \
  --location="us-east4" \
  --type="firestore-native"

# Create Firestore Index for authorizations (audience ASC, subject ASC)
gcloud firestore indexes composite create \
  --database="serviceauthcentral" \
  --collection-group="authorizations" \
  --field-config="field-path=audience,order=ASCENDING" \
  --field-config="field-path=subject,order=ASCENDING"

# Create Firestore Index for authorizations (subject ASC, audience ASC)
gcloud firestore indexes composite create \
  --database="serviceauthcentral" \
  --collection-group="authorizations" \
  --field-config="field-path=subject,order=ASCENDING" \
  --field-config="field-path=audience,order=ASCENDING"

# Set TTL for 'keys' collection
gcloud firestore fields ttls update ttl \
  --collection-group="keys" \
  --database="serviceauthcentral" \
  --enable-ttl

# Set TTL for 'loginCodes' collection
gcloud firestore fields ttls update ttl \
  --collection-group="loginCodes" \
  --database="serviceauthcentral" \
  --enable-ttl

# Set TTL for 'loginStates' collection
gcloud firestore fields ttls update ttl \
  --collection-group="loginStates" \
  --database="serviceauthcentral" \
  --enable-ttl
```

The KMS key ring and key are used for signing and verifying JWTs.  The key ring and key can be created using the OpenTofu module [serviceauthcentral-kms-gcp-tofu](https://github.com/UnitVectorY-Labs/serviceauthcentral-kms-gcp-tofu). The recommendation here would be to use a global location for the key ring and key so it can be used in any region. 

**Manual deployment of KMS keyring and signing key with `gcloud`**

```bash
# Create the KMS key ring
gcloud kms keyrings create "serviceauthcentral-key-ring" \
  --location="global"

# Create the KMS asymmetric signing key
gcloud kms keys create "serviceauthcentral-sign-key" \
  --location="global" \
  --keyring="serviceauthcentral-key-ring" \
  --purpose="asymmetric-signing" \
  --destroy-scheduled-duration="1d" \
  --protection-level="software" \
  --default-algorithm="rsa-sign-pkcs1-2048-sha256" \
  --skip-initial-version-creation
```

## Deploying ServiceAuthCentral Token API

The main data plane for ServiceAuthCentral is the Token API.  This API is responsible for issuing and validating JWTs.  The Token API can be deployed to GCP Cloud Run using the OpenTofu module [serviceauthcentral-token-gcp-tofu](https://github.com/UnitVectorY-Labs/serviceauthcentral-token-gcp-tofu).

Alternatively the docker image can be deployed manually:

> {: .warning }
> The below version deploys the 'dev' tag which is the latest development version. At this time there is no stable release.  See the latest releases on the [GitHub releases page](https://github.com/UnitVectorY-Labs/ServiceAuthCentral/pkgs/container/serviceauthcentral-token) for the latest version.

```bash
docker pull ghcr.io/unitvectory-labs/serviceauthcentral-token:dev
```

The token server is configured using envirionment variables as outlined on the [configuration]({{ site.baseurl }}{% link setupguide/configuration.md %}) guide.

## Deploying ServiceAuthCentral Manage API

The control plane for ServiceAuthCentral is the Manage API.  This API is responsible for managing the clients and authorizations for ServiceAuthCentral through a GraphQL API.  The Manage API can be deployed to GCP Cloud Run using the OpenTofu module [serviceauthcentral-manage-gcp-tofu](https://github.com/UnitVectorY-Labs/serviceauthcentral-manage-gcp-tofu).

Alternatively the docker image can be deployed manually:

> {: .warning }
> The below version deploys the 'dev' tag which is the latest development version. At this time there is no stable release.  See the latest releases on the [GitHub releases page](https://github.com/UnitVectorY-Labs/ServiceAuthCentral/pkgs/container/serviceauthcentral-manage) for the latest version.

```bash
docker pull ghcr.io/unitvectory-labs/serviceauthcentral-manage:dev
```

The manage server is configured using envirionment variables as outlined on the [configuration]({{ site.baseurl }}{% link setupguide/configuration.md %}) guide.

## Deploying ServiceAuthCentral Web Portal

The web portal for ServiceAuthCentral is a static website, [serviceauthcentralweb](https://github.com/UnitVectorY-Labs/serviceauthcentralweb), which can be deployed a variety of ways.

The portal is a Vue 3 application that must be compiled with the appropriate `.env.production` configuration. The compiled static website can then be served.

The web portal is configured using envirionment variables as outlined on the [configuration]({{ site.baseurl }}{% link setupguide/configuration.md %}) guide.
