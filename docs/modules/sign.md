---
layout: default
title: Sign
parent: Modules
nav_order: 6
---

# Sign

The sign module provides the interfaces for accessing and using the public keyes used for signing JWTs so that the underlying implementation can be swapped out as a runtime dependency.

## Sign Implementations

There are multiple data model implementations that are available. Exactly one module must be enabled at runtime.

> {: .important }
> Each module implementation will have additional environment variables that are required for it to work correctly when it is enabled.

- [Sign - GCP]({{ site.baseurl }}{% link modules/signgcp.md %}): Utilizing GCP's KMS service for managing and signing JWTs
- [Sign - Local]({{ site.baseurl }}{% link modules/signlocal.md %}): Utilize a local RSA key for signing JWTs
