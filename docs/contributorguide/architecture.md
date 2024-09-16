# Architecture

The architecture of ServiceAuthCentral takes an opinionated approach to eliminate secrets managed by both ServiceAuthCentral and other applications.  The goal of facilitating server-to-server authentication is to provide a secure and scalable solution that can be deployed in a variety of environments.  The architecture is designed to be modular and extendable to meet the needs of a variety of use cases.

## Key Components

A deployment of ServiceAuthCentral consists of the following components:

- Token Server (Container, REST API)
- Manage Server (Container, GraphQL API)
- Management Web Portal (Static Website)
- Database (Firestore)
- Key Management Service (GCP KMS)

## Elimination of Secrets

The motivation behind both the design and implementation of ServiceAuthCentral and the applications that utilize it is to eliminate the need for secrets to be managed by both ServiceAuthCentral and the applications that utilize it.

Within ServiceAuthCentral this is accomplished through the following mechanisms:

- The management and signing of JWTs is done using a KMS key stored in GCP KMS.  This allows for the private key used for signing to be managed by GCP KMS and not stored or accessible by the application making it impossible for the application server or administrator to inadvertently expose the private key.
- Access to the required components including the database and KMS are done through Service Accounts without the need for secrets to be stored in the application configuration.
- In the case that a client does require a client secret, that is stored in Firestore has a hashed value with unique salts for each client.

For clients utilizing ServiceAuthCentral the primary way of eliminating secrets is through the use of the JWT Bearer flow for authenticating to ServiceAuthCentral.  This is made possible by registering the JWKS, issuer, subject, and audience expected for a JWT for a specific client. This allows a variety of clients to authenticate to ServiceAuthCentral without the need for a client secret.

For example, a workload running on GCP can use its service account to vend an identity token which can be used to authenticate to ServiceAuthCentral.  This allows for the workload to authenticate to ServiceAuthCentral without the need for a client secret and request a token for a service it is authorized to access. Putting these pieces together this allows for a system to be constructed where there are no persistent secrets are needed.
