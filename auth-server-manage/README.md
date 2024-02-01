# auth-server-manage

This Spring Boot 3 application hosts the control plane of ServiceAuthCentral providing the GraphQL interface.

This implementation utilizes a modular architecture for the underlying database implementing the `auth-datamodel` interfaces. This means a specific implementation for these interfaces must be enabled at runtime which is accomplished through Spring Profiles.

## Configuration

The following configuration attributes:

| Property         | Required | Description        |
| ---------------- | -------- | ------------------ |
| sac.issuer       | Yes      | The JWT issuer url |
| sac.cors.origins | Yes      | CORS origins       |
