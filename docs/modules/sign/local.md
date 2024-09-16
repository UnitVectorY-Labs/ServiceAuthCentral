# Sign - Local

The sign local module provides a local implementation of the sign interface so that the underlying implementation can be swapped out as a runtime dependency.

## Configuration

This module is enabled by setting the `SPRING_PROFILES_ACTIVE` to include the profile `sign-local`.

The following environment variables are used by the sign local module:

| Environment Variable                   | Required | Description          |
| -------------------------------------- | -------- | -------------------- |
| sac.sign.local.active.kid              | Yes      | Active Key ID        |
| sac.sign.local.key1.privatekey         | Yes      | Key 1 Private Key    |
| sac.sign.local.key1.publickey          | Yes      | Key 1 Public Key     |
| sac.sign.local.key1.kid                | Yes      | Key 1 Key ID         |
