module com.unitvectory.auth.datamodule.gcp {

	// exports com.unitvectory.auth.datamodel.gcp.repository;

	requires lombok;
	requires org.mapstruct;

	requires java.base;

	requires google.cloud.core;
	requires google.cloud.firestore;
	requires com.google.common;
	requires com.google.api.apicommon;
	requires com.google.api.client;

	requires com.unitvectory.auth.datamodelauth.datamodule;

	opens com.unitvectory.auth.datamodel.gcp.repository to org.mapstruct;

	exports com.unitvectory.auth.datamodel.gcp.repository;

	// TODO: Goal is to fully encapsulate this implementation
	// provides com.unitvectory.auth.datamodel.repository.AuthorizationRepository
	// with
	// com.unitvectory.auth.datamodel.gcp.repository.FirestoreAuthorizationRepository;

	// provides com.unitvectory.auth.datamodel.repository.ClientRepository
	// with com.unitvectory.auth.datamodel.gcp.repository.FirestoreClientRepository;

	// provides com.unitvectory.auth.datamodel.repository.JwkCacheRepository
	// with
	// com.unitvectory.auth.datamodel.gcp.repository.FirestoreJwkCacheRepository;

}
