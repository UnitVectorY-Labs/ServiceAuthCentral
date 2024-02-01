# auth-datamodel-gcp

This library provides a [GCP Firestore](https://cloud.google.com/firestore) implementation of the `auth-datamodel` interfaces.

The design here uses runtime dependency injection allowing for the database implementation to be replaced.

## Spring Boot Profile

Spring Boot 3's dependency injection is used to initialize the relevant Beans for interacting with Firestore. This is accomplished through profiles.

The `datamodel-gcp` profile is enabled to utilize GCP Firestore.

## Configuration

The following configuration attributes:

| Property                                    | Required                       | Description               |
| ------------------------------------------- | ------------------------------ | ------------------------- |
| google.cloud.project                        | Yes                            | GCP Project name          |
| sac.datamodel.gcp.collection.authorizations | No (default: 'authorizations') | Firestore collection name |
| sac.datamodel.gcp.collection.clients        | No (default: 'clients')        | Firestore collection name |
| sac.datamodel.gcp.collection.keys           | No (default: 'keys')           | Firestore collection name |
| sac.datamodel.gcp.collection.logincodes     | No (default: 'logincodes')     | Firestore collection name |
| sac.datamodel.gcp.collection.loginstates    | No (default: 'loginStates')    | Firestore collection name |
