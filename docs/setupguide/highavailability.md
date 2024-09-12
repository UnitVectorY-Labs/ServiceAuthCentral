# High Availability

The nature of being an OAuth 2.0 server that microservices depend on for authentication and authorization means that ServiceAuthCentral needs to be highly available. While for some deployments a single region may be sufficient, for others a multi-region deployment may be necessary.

## Multi-Region Deployment of Firestore

The current datastore implemented by ServiceAuthCentral is Firestore. While Firestore offers a few [multi-region locations](https://cloud.google.com/datastore/docs/locations#location-mr) this may not be sufficient for some deployments. This is where [crossfiresync](https://github.com/UnitVectorY-Labs/crossfiresyncrun) comes in. Crossfiresync is a tool that can be used to synchronize Firestore data between multiple regions in near real-time allowing for a multi-region deployment of Firestore. This is possible because ServiceAuthCentral is overwhelmingly a read-heavy application and Firestore is optimized for reads.

Crossfiresync requires multiple components to be set up correctly including Pub/Sub topics and subscriptions and the application itself which facilitates the replication.  Therefore it is recommended to use the OpenTofu module [crossfiresyncrun-tofu](https://github.com/UnitVectorY-Labs/crossfiresyncrun-tofu) to set it manually.  The [serviceauthcentral-gcp-tofu](https://github.com/UnitVectorY-Labs/serviceauthcentral-gcp-tofu) module which sets up a complete ServiceAuthCentral deployment includes the crossfiresyncrun-tofu module as a submodule and will set up crossfiresync for you as part of the recommended deployment.
