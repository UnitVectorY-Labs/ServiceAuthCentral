auth-datamodel-gcp
==================

This library provides a GCP Firestore implementation of the `auth-datamodel` interfaces.

The design here uses runtime dependency injection allowing for the database implementation to be replaced.

Spring Boot Profile
-------------------

Spring Boot 3's dependency injection is used to initialize the relevant Beans for interacting with Firestore.  This is accomplished through profiles.

The `datamodel-gcp` profile is enabled to utilize GCP Firestore.


Configuration
-------------

The following configuration attributes:

| Property                                                   | Default        | Required | Description               |
|------------------------------------------------------------|----------------|----------|---------------------------|
| google.cloud.project                                       |                | Yes      | GCP Project name          |
| serviceauthcentral.datamodel.gcp.collection.authorizations | authorizations | Yes      | Firestore collection name |
| serviceauthcentral.datamodel.gcp.collection.clients        | clients        | Yes      | Firestore collection name |
| serviceauthcentral.datamodel.gcp.collection.keys           | keys           | Yes      | Firestore collection name |
