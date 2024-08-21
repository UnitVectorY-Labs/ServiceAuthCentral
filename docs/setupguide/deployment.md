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

<details>
<summary>Manual deployment of Firestore database with `gcloud`</summary>

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
</details>
