module com.unitvectory.auth.server.manage {

	// exports com.unitvectory.auth.datamodel.gcp.repository;

	requires lombok;
	requires org.mapstruct;

	requires java.base;

	requires spring.boot.starter.web;
	requires spring.boot.starter.graphql;

	requires spring.graphql;
	requires spring.boot;
	requires spring.boot.autoconfigure;

	requires spring.beans;
	requires spring.context;

	requires com.graphqljava;

	requires reactor.core;

	requires com.unitvectory.auth.datamodelauth.datamodule;
	requires com.unitvectory.auth.datamodule.gcp;
	// requires com.unitvectory.

	// TODO: Goal is to remove any direct GCP depdencny

	requires google.cloud.core;
	requires google.cloud.firestore;
	requires com.google.common;
	requires com.google.api.apicommon;
	requires com.google.api.client;

}
