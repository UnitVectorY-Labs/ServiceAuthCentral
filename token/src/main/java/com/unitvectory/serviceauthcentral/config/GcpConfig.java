package com.unitvectory.serviceauthcentral.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.kms.v1.KeyManagementServiceClient;

import lombok.Getter;

@Configuration
@Profile("!test")
public class GcpConfig {

	@Getter
	@Value("${google.cloud.project}")
	private String projectId;

	@Autowired
	private AppConfig appConfig;

	@Bean
	public String keyManagementServiceKeyName() {
		// Build the name of the Cloud KMS key that will be used to sign JWTs.
		// This does not include the version as multiple versions are utilized to allow
		// for key rotations without service interruption
		return "projects/" + this.projectId + "/locations/" + this.appConfig.getKeyLocation() + "/keyRings/"
				+ this.appConfig.getKeyRing() + "/cryptoKeys/" + this.appConfig.getKeyName();
	}

	@Bean
	public KeyManagementServiceClient keyManagementServiceClient() {
		try {
			return KeyManagementServiceClient.create();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Bean
	public Firestore firestore() {
		FirestoreOptions firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
				.setProjectId(this.getProjectId()).build();
		return firestoreOptions.getService();
	}
}
