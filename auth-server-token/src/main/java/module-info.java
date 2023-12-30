module com.unitvectory.auth.server.token {

	requires lombok;
	requires org.mapstruct;

	requires java.base;

	requires spring.boot.starter.web;
	requires spring.boot;
	requires spring.boot.autoconfigure;

	requires spring.web;
	requires spring.core;
	requires spring.beans;
	requires spring.context;

	requires spring.boot.starter.cache;

	requires jakarta.validation;
	requires com.fasterxml.jackson.databind;
	requires com.github.benmanes.caffeine;

	requires com.unitvectory.auth.datamodelauth.datamodule;
	requires com.unitvectory.auth.datamodule.gcp;

	requires com.auth0.jwt;
	requires com.fasterxml.jackson.annotation;

	requires proto.google.common.protos;
	requires protobuf.java;
	requires proto.google.cloud.kms.v1;
	requires google.cloud.kms; // Check for the correct module name
	requires google.cloud.core.grpc; // Check if this includes protobuf
	requires google.cloud.core;
	requires google.cloud.firestore;
	requires com.google.common;
	requires com.google.api.apicommon;
	requires com.google.api.client;

	// Add the correct modules based on library documentation
	// requires auth0.jwt; // Placeholder for the correct Auth0 module
	// requires google.protobuf; // Placeholder for the correct Protobuf module
	// requires spring.cache; // Placeholder for the correct Spring Cache module
}
