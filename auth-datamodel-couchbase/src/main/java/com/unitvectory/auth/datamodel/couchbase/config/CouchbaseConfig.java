package com.unitvectory.auth.datamodel.couchbase.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.couchbase.client.java.Cluster;

@Configuration
@Profile("datamodel-couchbase")
public class CouchbaseConfig {

	@Value("${serviceauthcentral.datamodel.couchbase.connection}")
	private String connection;

	@Value("${serviceauthcentral.datamodel.couchbase.user}")
	private String user;

	@Value("${serviceauthcentral.datamodel.couchbase.password}")
	private String password;

	@Bean
	public Cluster couchbaseCluster() {
		return Cluster.connect(this.connection, this.user, this.password);
	}
}
