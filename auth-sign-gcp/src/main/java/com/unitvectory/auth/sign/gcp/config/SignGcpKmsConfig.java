/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.unitvectory.auth.sign.gcp.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.cloud.kms.v1.KeyManagementServiceClient;

/**
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
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
