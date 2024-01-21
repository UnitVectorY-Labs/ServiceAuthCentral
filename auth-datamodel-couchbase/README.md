# auth-datamodel-couchbase

This library provides a Couchbase implementation of the `auth-datamodel` interfaces for the `com.unitvectory.auth` package.

Utilizing the flexibility of Spring Boot's configuration and dependency injection, this setup enables the use of Couchbase as the database layer.

## Spring Boot Profile

The `datamodel-couchbase` profile is used for integrating Couchbase with the application. This is controlled by Spring Boot's profile functionality, ensuring the appropriate configuration is loaded.

## Configuration

The application requires the following configuration properties for connecting and interacting with Couchbase:

| Property                                                           | Required | Default              | Description                             |
| ------------------------------------------------------------------ | -------- | -------------------- | --------------------------------------- |
| `serviceauthcentral.datamodel.couchbase.connection`                | Yes      |                      | Couchbase cluster connection string     |
| `serviceauthcentral.datamodel.couchbase.user`                      | Yes      |                      | Username for Couchbase authentication   |
| `serviceauthcentral.datamodel.couchbase.password`                  | Yes      |                      | Password for Couchbase authentication   |
| `serviceauthcentral.datamodel.couchbase.bucket`                    | No       | `serviceauthcentral` | Couchbase bucket name                   |
| `serviceauthcentral.datamodel.couchbase.scope`                     | No       | `serviceauthcentral` | Couchbase scope name                    |
| `serviceauthcentral.datamodel.couchbase.collection.authorizations` | No       | `authorizations`     | Couchbase collection for authorizations |
| `serviceauthcentral.datamodel.couchbase.collection.clients`        | No       | `clients`            | Couchbase collection for clients        |
| `serviceauthcentral.datamodel.couchbase.collection.keys`           | No       | `keys`               | Couchbase collection for keys           |
| `serviceauthcentral.datamodel.couchbase.collection.logincodes`     | No       | `loginCodes`         | Couchbase collection for login codes    |
| `serviceauthcentral.datamodel.couchbase.collection.loginstates`    | No       | `loginStates`        | Couchbase collection for login states   |

## Bean Configurations

The `CouchbaseConfig` class initializes the `Cluster` bean based on the provided connection details. The `DatamodelCouchbaseConfig` class then utilizes this `Cluster` bean to create specific repositories:

- `AuthorizationRepository`: Manages authorizations.
- `ClientRepository`: Handles client data.
- `JwkCacheRepository`: Caches JSON Web Keys.
- `LoginCodeRepository`: Handles authorization codes.
- `LoginStateRepository`: Handles login states.

## Couchbase Repositories

These repositories interact with their respective Couchbase collections, facilitating the core operations of the `auth-datamodel`.

## Couchbase Indexes

Indexes need to be created to allow for the required queries.
This assumes the default bucket, scope, and collection names are used.

```
CREATE INDEX idx_subject ON `serviceauthcentral`.`serviceauthcentral`.`clients`(clientId);
```

```
CREATE INDEX idx_subject ON `serviceauthcentral`.`serviceauthcentral`.`authorizations`(subject);
```

```
CREATE INDEX idx_audience ON `serviceauthcentral`.`serviceauthcentral`.`authorizations`(audience);
```

```
CREATE INDEX idx_url ON `serviceauthcentral`.`serviceauthcentral`.`keys`(url);
```
