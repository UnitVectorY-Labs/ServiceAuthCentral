---
layout: default
title: Integration Guide
nav_order: 3
has_children: true
permalink: /integrationguide
---

# Integration Guide

ServiceAuthCentral provides an opinionated way to deploy an OAuth 2.0 server authorization server to facilitate server-to-server authentication and authorization between a diverse set of microservices.  The following guide will walk through the steps to integrate a `Client Service` which to request an access token from the `Token Server` to access a `Resource Service` which will then validate the token.  One key aspect of ServiceAuthCentral is it is intended that a microservice would use a single client id per microservice and use that as a resource server that other clients are able to access and as a client to access other resource servers allowing it to have a single identity within the system.
