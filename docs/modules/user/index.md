# User

The user module provides the interfaces for authenticating users to the management API using different external OAuth 2.0 implementations.  Multiple user implementations can be enabled at the same time at runtime.

## User Interfaces

The following interfaces are implemented to provide data persistence.

- `LoginUserService`: Interface for interacting with the user service for login

## User Implementations

There are multiple user implementations that are available. Multiple user implementations can be enabled at runtime.

- [User - GitHub](./github.md): Login with a GitHub Account
- [User - Google](./google.md): Login with a Google Account

