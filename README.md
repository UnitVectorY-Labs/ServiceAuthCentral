[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Work In Progress](https://img.shields.io/badge/Status-Work%20In%20Progress-yellow)](https://unitvectory-labs.github.io/uvy-labs-guide/bestpractices/status.html#work-in-progress)

# ServiceAuthCentral

Simplify microservice security with ServiceAuthCentral: Centralized, open-source authorization in the cloud, minus the shared secrets.

## Overview

This application implements the OAuth 2.0 Client Credentials flow for the purpose of machine-to-machine authentication and authorization acting as a centralized authorization server. This means that for one client to vend an access token, in the form of a JWT, with the audience of another client, that authorization must be granted in this centralized server. This allows the servers verifying client requests to trust the client based on the JWT being valid from this authorization server.

The primary design objective of this project is that secrets are best avoided if possible. Every design decision is based around eliminating or minimizing the possibility of having any secrets that can be accidentally exposed.

This application is a SpringBoot application designed to run on GCP utilizing Firestore as the primary database and Cloud Key Management Service for signing JWT. By off loading the responsibility of managing the private key for signing JWTs to Google, it is less likely that key can be leaked.

The desire to eliminate secrets does not stop there. While the standard client_id and client_secret can be used, an alternative authentication mechanism that utilizes a JWT from the calling client, such as a GCP Service Account, can authenticate credentials exchanges to the authorization server in place of a client_secret. This eliminates the need for secrets to be stored when these JWTs can be utilized.

## Related Project

The front end web application for this is implemented in [UnitVectorY-Labs/serviceauthcentralweb](https://github.com/UnitVectorY-Labs/serviceauthcentralweb)

## Data Plane

The data plane for ServiceAuthCentral provides the token endpoint for exchanging tokens.

## Control Plane

The control plane for SerivceAuthCentral is a GraphQL API to manage the clients and authorizations.
