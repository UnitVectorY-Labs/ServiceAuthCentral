# Data Model - Memory

The data model memory module provides an in memory implementation of the the data model interfaces so that the underlying implementation can be swapped out as a runtime dependency.

This module is not practical for a production deployment but is useful for testing and development.

## Spring Boot Profile

Spring Boot 3's dependency injection is used to initialize the relevant Beans for using memory data model. This is accomplished through profiles.

The `datamodel-memory` profile is enabled to utilize the memory data model.
