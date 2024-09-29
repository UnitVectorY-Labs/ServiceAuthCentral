# Modules

ServiceAuthCentral is built as a modular application using Java and Maven with Spring Boot 3. There are a number of different modules that comprise the application and are described in this section. This design allows for modules to be swapped out at runtime to provide different implementations of the same functionality. This is useful for testing and development as well as for production deployments where different implementations may be required.

- [Token Server](tokenserver.md)
- [Manage Server](manageserver.md)
- [Data Model - Index](datamodel/index.md)
- [Data Model - Firestore](datamodel/firestore.md)
- [Data Model - In-memory](datamodel/memory.md)
- [Sign - Index](sign/index.md)
- [Sign - GCP](sign/gcp.md)
- [Sign - Local](sign/local.md)
- [User - Index](user/index.md)
- [User - GitHub](user/github.md)
- [User - Google](user/google.md)
- [Verify - Index](verify/index.md)
- [Verify - Auth0](verify/auth0.md)
- [Utility](util.md)
