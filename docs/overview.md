---
layout: default
title: Overview
nav_order: 1
---

# ServiceAuthCentral

ServiceAuthCentral is a tool designed to streamline authorization for microservices. Here's what makes it stand out:

- **Open Source:** Freely available consisting of a centralized authorization server, management server, and web based management frontend.
- **Server-to-Server Authentication:** Targets solving server-to-server authentication for microservices using the client credentials OAuth 2.0 flow.
- **Opinionated Centralized Authorization:** Provides centralized capability for manage authorizations for microservices accessing eachother simplifying the process of authorization.
- **Elimination of Shared Secrets:** Advocates for the urn:ietf:params:oauth:grant-type:jwt-bearer flow to reduce reliance on shared secrets. Utilizing cloud based solutions such as GCP service accounts can eliminate the need for shared secrets.
- **Modular Cloud-Native Backend:** Utilizes a module backend to support cloud native technologies for data persistence and utilizes cloud services such as KMS for JWT signing eliminating direct access to power secrets.
