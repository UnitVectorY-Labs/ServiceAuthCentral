package com.unitvectory.serviceauthcentral.config;

import java.io.IOException;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.kms.v1.KeyManagementServiceClient;

@TestConfiguration
@Profile("test")
public class TestGcpConfig {

	@Bean
	public String keyManagementServiceKeyName() {
		return "test/key/name";
	}

	@Bean
	public KeyManagementServiceClient keyManagementServiceClient() throws IOException {
		return Mockito.mock(KeyManagementServiceClient.class);
	}

	@Bean
	public Firestore firestore() {
		return Mockito.mock(Firestore.class);
	}
}
