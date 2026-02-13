# Data Model - Valkey

The data model Valkey module provides a [Valkey](https://valkey.io/) implementation of the data model interfaces so that the underlying implementation can be swapped out as a runtime dependency.

Valkey is an open source, high-performance key/value datastore that is compatible with Redis. This module uses Spring Data Redis with the Lettuce client to connect to Valkey.

## Data Storage

Data is stored in Valkey using Hash structures with the following key prefixes:

- `sac:client:{clientId}` - Client records
- `sac:clients` - Sorted set index for client pagination
- `sac:auth:{documentId}` - Authorization records
- `sac:auth:subject:{subject}` - Set index of authorizations by subject
- `sac:auth:audience:{audience}` - Set index of authorizations by audience
- `sac:auth:lookup:{subjectHash}:{audienceHash}` - Lookup key for authorization by subject and audience
- `sac:loginstate:{sessionId}` - Login state records (with TTL)
- `sac:logincode:{code}` - Login code records (with TTL)
- `sac:jwk:{url}:{kid}` - Cached JWK records (with TTL)
- `sac:jwk:url:{url}` - Set index of JWK kids by URL

## Spring Boot Profile

Spring Boot 3's dependency injection is used to initialize the relevant Beans for interacting with Valkey. This is accomplished through profiles.

The `datamodel-valkey` profile is enabled to utilize Valkey.

## Configuration

The following configuration attributes are available:

| Property            | Required | Description               |
| ------------------- | -------- | ------------------------- |
| spring.data.redis.host | Yes   | Valkey server hostname    |
| spring.data.redis.port | No (default: 6379) | Valkey server port |
| spring.data.redis.password | No | Valkey server password   |
| spring.data.redis.ssl.enabled | No (default: false) | Enable SSL/TLS |
