---
layout: default
title: Verify
nav_order: 6
has_children: true
permalink: /verify
---

# Verify

The token server allows for verification of external JWT tokens for the `urn:ietf:params:oauth:grant-type:jwt-bearer` OAuth 2.0 flow.

## Configuration

There is only one implementation available for the verify component.

The `SPRING_PROFILES_ACTIVE` environment variable needs be set to include the value `verify-auth0` for the implementation that utilizes the Auth0 Java client library.
