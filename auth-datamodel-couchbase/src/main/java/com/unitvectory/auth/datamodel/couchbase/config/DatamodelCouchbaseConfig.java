package com.unitvectory.auth.datamodel.couchbase.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.unitvectory.auth.datamodel.couchbase.repository.CouchbaseAuthorizationRepository;
import com.unitvectory.auth.datamodel.couchbase.repository.CouchbaseClientRepository;
import com.unitvectory.auth.datamodel.couchbase.repository.CouchbaseJwkCacheRepository;
import com.unitvectory.auth.datamodel.repository.AuthorizationRepository;
import com.unitvectory.auth.datamodel.repository.ClientRepository;
import com.unitvectory.auth.datamodel.repository.JwkCacheRepository;

@Configuration
@Profile("datamodel-couchbase")
public class DatamodelCouchbaseConfig {

	@Autowired
	private Cluster couchbaseCluster;

	@Value("${serviceauthcentral.datamodel.couchbase.bucket:serviceauthcentral}")
	private String bucket;

	@Value("${serviceauthcentral.datamodel.couchbase.scope:serviceauthcentral}")
	private String scope;

	@Value("${serviceauthcentral.datamodel.couchbase.collection.authorizations:authorizations}")
	private String collectionAuthorizations;

	@Value("${serviceauthcentral.datamodel.couchbase.collection.clients:clients}")
	private String collectionClients;

	@Value("${serviceauthcentral.datamodel.couchbase.collection.keys:keys}")
	private String collectionKeys;

	@Bean
	public AuthorizationRepository authorizationRepository() {
		Bucket bucket = this.couchbaseCluster.bucket(this.bucket);
		Scope scope = bucket.scope(this.scope);
		Collection collection = scope.collection(this.collectionAuthorizations);

		return new CouchbaseAuthorizationRepository(couchbaseCluster, collection);
	}

	@Bean
	public ClientRepository clientRepository() {
		Bucket bucket = this.couchbaseCluster.bucket(this.bucket);
		Scope scope = bucket.scope(this.scope);
		Collection collection = scope.collection(this.collectionClients);

		return new CouchbaseClientRepository(couchbaseCluster, collection);
	}

	@Bean
	public JwkCacheRepository jwkCacheRepository() {
		Bucket bucket = this.couchbaseCluster.bucket(this.bucket);
		Scope scope = bucket.scope(this.scope);
		Collection collection = scope.collection(this.collectionKeys);

		return new CouchbaseJwkCacheRepository(couchbaseCluster, collection);
	}
}
