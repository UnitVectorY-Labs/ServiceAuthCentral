# Modules

The primary use case for ServiceAuthCentral is to act as a backend for server-to-server authentication. ServiceAuthCentral has one primary [repository](https://github.com/UnitVectorY-Labs/ServiceAuthCentral) that is divided into multiple Java modules that implement the primary functionality of the application. The implementation is a Java 17 Spring Boot 3 application that utilizes REST for the token endpoints and GraphQL for the management API.

The main application is composed of Java modules which are described in the [modules](../modules/index.md) section of the documentation.  The main components being the token server, which is the data plane, and the manage server, which is the control plane.

For the management aspect a web portal is provided with the [serviceauthcentralweb](https://github.com/UnitVectorY-Labs/serviceauthcentralweb).  This is implemented using Vue 3 with bootstrap for the interface and Appolo for interfacing with the GraphQL API.

## OpenTofu / Terraform Modules

To aid in the deployment there are a number of different Tofu (Terraform) modules that aid in the deployment.

- [serviceauthcentral-gcp-tofu](https://github.com/UnitVectorY-Labs/serviceauthcentral-gcp-tofu) - OpenTofu module for deploying a fully working ServiceAuthCentral deployment in GCP

The functionality depends on a number of submodules.

- [serviceauthcentral-token-gcp-tofu](https://github.com/UnitVectorY-Labs/serviceauthcentral-token-gcp-tofu) - OpenTofu module for deploying ServiceAuthCentral token API to Cloud Run in GCP
- [serviceauthcentral-manage-gcp-tofu](https://github.com/UnitVectorY-Labs/serviceauthcentral-manage-gcp-tofu) - OpenTofu module for deploying ServiceAuthCentral manage API to Cloud Run in GCP
- [serviceauthcentral-kms-gcp-tofu](https://github.com/UnitVectorY-Labs/serviceauthcentral-kms-gcp-tofu) - OpenTofu module for deploying ServiceAuthCentral KMS Keys in GCP
- [serviceauthcentral-firestore-gcp-tofu](https://github.com/UnitVectorY-Labs/serviceauthcentral-firestore-gcp-tofu) - OpenTofu module for deploying ServiceAuthCentral Firestore Database
- [serviceauthcentral-firestore-bootstrap-gcp-tofu](https://github.com/UnitVectorY-Labs/serviceauthcentral-firestore-bootstrap-gcp-tofu) - OpenTofu module for deploying ServiceAuthCentral Firestore records needed to bootstrap an install
- [serviceauthcentral-workload-identity-gcp-tofu](https://github.com/UnitVectorY-Labs/serviceauthcentral-workload-identity-gcp-tofu) - OpenTofu module for deploying ServiceAuthCentral Workload Identity Federation in GCP
