# ServiceAuthCentral

Cloud based client credentials authorization server with no-secret authentication support

## Overview

This application implements the OAuth 2.0 Client Credentials flow for the purpose of machine-to-machine authentication and authorization acting as a centralized authorization server. This means that for one client to vend an access token, in the form of a JWT, with the audience of another client, that authorization must be granted in this centralized server.  This allows the servers verifying client requests to trust the client based on the JWT being valid from this authorization server.

The primary design objective of this project is that secrets are best avoided if possible.  Every design decision is based around eliminating or minimizing the possibility of having any secrets that can be accidentally exposed.

This application is a SpringBoot application designed to run on GCP utilizing Firestore as the primary database and Cloud Key Management Service for signing JWT. By off loading the responsibility of managing the private key for signing JWTs to Google, it is less likely that key can be leaked.

The desire to eliminate secrets does not stop there.  While the standard client_id and client_secret can be used, an alternative authentication mechanism that utilizes a JWT from the calling client, such as a GCP Service Account, can authenticate credentials exchanges to the authorization server in place of a client_secret. This eliminates the need for secrets to be stored when these JWTs can be utilized.

## Data Plane

The data plane for ServiceAuthCentral provides the token endpoint for exchanging tokens.

## Control Plane

The control plane for SerivceAuthCentral is a GraphQL API to manage the clients and authorizations.
