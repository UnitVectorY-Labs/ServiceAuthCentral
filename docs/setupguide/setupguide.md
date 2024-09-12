---
layout: default
title: Setup Guide
nav_order: 2
has_children: true
permalink: /setupguide
---

# Setup Guide

[ServiceAuthCentral](https://github.com/UnitVectorY-Labs/ServiceAuthCentral) is deployed as a set of Docker containers and a static website for the [management portal](https://github.com/UnitVectorY-Labs/serviceauthcentralweb). This guide will walk you through the steps to deploy the service. While the design of ServiceAuthCentral is modular and is intended to be extendable for deployment in a variety of environments, the initial version focuses on deployments to GCP.

> {: .important }
> To get up and running read the [deployment](../setupguide/deployment.md) and [configuration](../setupguide/configuration.md) guides.

## OpenTofu / Terraform Modules

For reference the following OpenTofu modules are available for deploying ServiceAuthCentral to GCP:

- [serviceauthcentral-gcp-tofu](https://github.com/UnitVectorY-Labs/serviceauthcentral-gcp-tofu): OpenTofu module for deploying a fully working ServiceAuthCentral deployment
- [serviceauthcentral-token-gcp-tofu](https://github.com/UnitVectorY-Labs/serviceauthcentral-token-gcp-tofu): OpenTofu module for deploying ServiceAuthCentral token API
- [serviceauthcentral-manage-gcp-tofu](https://github.com/UnitVectorY-Labs/serviceauthcentral-manage-gcp-tofu): OpenTofu module for deploying ServiceAuthCentral manage API
- [serviceauthcentral-firestore-gcp-tofu](https://github.com/UnitVectorY-Labs/serviceauthcentral-firestore-gcp-tofu): OpenTofu module for deploying ServiceAuthCentral Firestore Database
- [serviceauthcentral-kms-gcp-tofu](https://github.com/UnitVectorY-Labs/serviceauthcentral-kms-gcp-tofu): OpenTofu module for deploying ServiceAuthCentral KMS Keys
