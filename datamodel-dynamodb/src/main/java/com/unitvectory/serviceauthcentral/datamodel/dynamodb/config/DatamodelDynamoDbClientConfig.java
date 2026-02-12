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
package com.unitvectory.serviceauthcentral.datamodel.dynamodb.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

/**
 * The data model config for AWS DynamoDB client
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Configuration
@Profile("datamodel-dynamodb")
public class DatamodelDynamoDbClientConfig {

	@Value("${aws.region:us-east-1}")
	private String awsRegion;

	@Value("${aws.dynamodb.endpoint:}")
	private String dynamoDbEndpoint;

	@Bean
	public DynamoDbClient dynamoDbClient() {
		DynamoDbClientBuilder builder = DynamoDbClient.builder()
				.region(Region.of(awsRegion));

		if (dynamoDbEndpoint != null && !dynamoDbEndpoint.isEmpty()) {
			// For local DynamoDB testing
			builder.endpointOverride(URI.create(dynamoDbEndpoint))
					.credentialsProvider(StaticCredentialsProvider.create(
							AwsBasicCredentials.create("dummy", "dummy")));
		} else {
			builder.credentialsProvider(DefaultCredentialsProvider.create());
		}

		return builder.build();
	}

	@Bean
	public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
		return DynamoDbEnhancedClient.builder()
				.dynamoDbClient(dynamoDbClient)
				.build();
	}
}
