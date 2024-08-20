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
<summary>gcloud Firestore Deployment</summary>

# Create Firestore database (if needed)
gcloud firestore databases create --region="your-region" --type="NATIVE"

# Create Firestore Index for authorizations (audience ASC, subject ASC)
gcloud firestore indexes composite create \
  --collection-group="authorizations" \
  --fields="audience:ASCENDING,subject:ASCENDING"

# Create Firestore Index for authorizations (subject ASC, audience ASC)
gcloud firestore indexes composite create \
  --collection-group="authorizations" \
  --fields="subject:ASCENDING,audience:ASCENDING"

# Set TTL for 'keys' collection
gcloud firestore fields update \
  "databases/sac/collectionGroups/keys/fields/ttl" \
  --ttl-config-state=ENABLED

# Set TTL for 'loginCodes' collection
gcloud firestore fields update \
  "databases/sac/collectionGroups/loginCodes/fields/ttl" \
  --ttl-config-state=ENABLED

# Set TTL for 'loginStates' collection
gcloud firestore fields update \
  "databases/sac/collectionGroups/loginStates/fields/ttl" \
  --ttl-config-state=ENABLED
</details>
