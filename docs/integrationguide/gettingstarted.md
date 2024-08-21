---
layout: default
title: Getting Started
parent: Integration Guide
nav_order: 1
---

# Getting Started

Once the ServiceAuthCentral token service is deployed, you can start integrating your services with it.  This guide will walk through the steps to integrate a `Client Service` which to request an access token from the `Token Server` to access a `Resource Service` which will then validate the token.

## Client Service

A `Client Service` is a microservice that is requesting an access token from the `Token Server` to access a `Resource Service`.  The `Client Service` will need to be able to make an HTTP request to the `Token Server` to request an access token and then use that access token to make an HTTP request to the `Resource Service`.

This request can be authenticated using either a client ID and client secret (which is discouraged) or a JWT token from a trusted issuer such as Google through a Service Account. 

### Requesting an Access Token with a Client ID and Client Secret

TODO

### Requesting an Access Token with a JWT Bearer Token

TODO

## Resource Service

TODO
