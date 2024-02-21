---
layout: default
title: Sign
nav_order: 5
has_children: true
permalink: /sign
---

# Sign

A flexible deployment is supported by allowing different underlying JWT signing mechanism based on the desired deployment mechanism.

## Configuration

One of the following implementations must be configured for the sign component.
This is done by including one of the following profiles in the `SPRING_PROFILES_ACTIVE` environment varilable.

> {: .important }
> Each module implementation may have additional environment variables that are required for it to work correctly when it is enabled.

- `sign-gcp` - Use GCP KMS to sign the JWTs
