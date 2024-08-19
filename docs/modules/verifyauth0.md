---
layout: default
title: Verify - Auth0
parent: Modules
nav_order: 13
---

# Verify - Auth0

The verify Auth0 module provides an implementation for verifying JWTs based on the Auth0 JWT library.

## Spring Boot Profile

Spring Boot 3's dependency injection is used to initialize the relevant Beans. This is accomplished through profiles for the other modules but only one verify implementation is available so it is automatically enabled.
