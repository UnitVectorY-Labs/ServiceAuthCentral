---
layout: default
title: Data Model
parent: Modules
nav_order: 3
---

# Data Model

A flexible deployment is supported by allowing different underlying database technologies based on the desired deployment mechanism.

## Configuration

One of the following implementations must be configured for the data store component.
This is done by including one of the following profiles in the `SPRING_PROFILES_ACTIVE` environment varilable.

> {: .important }
> Each module implementation may have additional environment variables that are required for it to work correctly when it is enabled.

- `datamodel-firestore` - Use GCP KMS to sign the JWTs
