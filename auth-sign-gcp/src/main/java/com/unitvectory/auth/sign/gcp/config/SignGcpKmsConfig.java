package com.unitvectory.auth.sign.gcp.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.cloud.kms.v1.KeyManagementServiceClient;

@Configuration
@Profile("sign-gcp")
public class SignGcpKmsConfig {

	@Value("${google.cloud.project}")
	private String projectId;

	@Value("${sac.sign.gcp.key.location:global}")
	private String keyLocation;

	@Value("${sac.sign.gcp.key.ring}")
	private String keyRing;

	@Value("${sac.sign.gcp.key.name}")
	private String keyName;

	@Bean
	public String keyManagementServiceKeyName() {
		// Build the name of the Cloud KMS key that will be used to sign JWTs.
		// This does not include the version as multiple versions are utilized to allow
		// for key rotations without service interruption
		return "projects/" + this.projectId + "/locations/" + this.keyLocation + "/keyRings/"
				+ this.keyRing + "/cryptoKeys/" + this.keyName;
	}

	@Bean
	public KeyManagementServiceClient keyManagementServiceClient() {
		try {
			return KeyManagementServiceClient.create();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
