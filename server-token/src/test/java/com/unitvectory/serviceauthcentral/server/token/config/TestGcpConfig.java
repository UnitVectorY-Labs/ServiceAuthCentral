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
package com.unitvectory.serviceauthcentral.server.token.config;

import java.io.IOException;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import com.google.cloud.kms.v1.KeyManagementServiceClient;

/**
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
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
}
