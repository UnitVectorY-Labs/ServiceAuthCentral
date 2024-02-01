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
import com.unitvectory.auth.datamodel.couchbase.repository.CouchbaseLoginCodeRepository;
import com.unitvectory.auth.datamodel.couchbase.repository.CouchbaseLoginStateRepository;
import com.unitvectory.auth.datamodel.repository.AuthorizationRepository;
import com.unitvectory.auth.datamodel.repository.ClientRepository;
import com.unitvectory.auth.datamodel.repository.JwkCacheRepository;
import com.unitvectory.auth.datamodel.repository.LoginCodeRepository;
import com.unitvectory.auth.datamodel.repository.LoginStateRepository;

@Configuration
@Profile("datamodel-couchbase")
public class DatamodelCouchbaseConfig {

	@Autowired
	private Cluster couchbaseCluster;

	@Value("${sac.datamodel.couchbase.bucket:serviceauthcentral}")
	private String bucket;

	@Value("${sac.datamodel.couchbase.scope:serviceauthcentral}")
	private String scope;

	@Value("${sac.datamodel.couchbase.collection.authorizations:authorizations}")
	private String collectionAuthorizations;

	@Value("${sac.datamodel.couchbase.collection.clients:clients}")
	private String collectionClients;

	@Value("${sac.datamodel.couchbase.collection.keys:keys}")
	private String collectionKeys;

	@Value("${sac.datamodel.couchbase.collection.logincodes:loginCodes}")
	private String collectionLoginCodes;

	@Value("${sac.datamodel.couchbase.collection.loginstate:loginStates}")
	private String collectionLoginStates;

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

	@Bean
	LoginCodeRepository loginCodeRepository() {
		Bucket bucket = this.couchbaseCluster.bucket(this.bucket);
		Scope scope = bucket.scope(this.scope);
		Collection collection = scope.collection(this.collectionLoginCodes);

		return new CouchbaseLoginCodeRepository(collection);
	}

	@Bean
	LoginStateRepository loginStateRepository() {
		Bucket bucket = this.couchbaseCluster.bucket(this.bucket);
		Scope scope = bucket.scope(this.scope);
		Collection collection = scope.collection(this.collectionLoginStates);

		return new CouchbaseLoginStateRepository(collection);
	}
}
