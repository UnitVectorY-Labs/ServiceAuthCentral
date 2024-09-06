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
package com.unitvectory.serviceauthcentral.server.manage.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.File;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.graphql.test.tester.GraphQlTester.Request;
import org.springframework.graphql.test.tester.GraphQlTester.Response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The abstract resolver test that utilizes JSON files to perform parameterized
 * tests for GraphQL.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public abstract class AbstractResolverTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    protected void runTest(HttpGraphQlTester httpGraphQlTester, String fileName) {
        try {

            JsonNode rootNode = objectMapper.readTree(new File(fileName));

            if (rootNode.isArray()) {
                // File is a list of tests
                for (JsonNode n : rootNode) {
                    validate(httpGraphQlTester, n);
                }
            } else {
                // File is a single test
                validate(httpGraphQlTester, rootNode);
            }

        } catch (Exception e) {
            fail(e);
        }
    }

    private void validate(HttpGraphQlTester httpGraphQlTester, JsonNode rootNode) throws Exception {
        String accessToken = rootNode.get("accessToken").asText();
        httpGraphQlTester = httpGraphQlTester.mutate().header("Authorization", "Bearer " + accessToken).build();

        String documentName = rootNode.get("documentName").asText();
        assertNotNull(documentName, "Test file must have a documentName property");
        Request<?> request = httpGraphQlTester.documentName(documentName);

        JsonNode parameters = rootNode.get("parameters");
        parameters.fieldNames().forEachRemaining(
                fieldName -> request.variable(fieldName, parameters.get(fieldName)));

        Response response = request.execute();

        String path = rootNode.get("path").asText();
        assertNotNull(path, "Test file must have a path property");

        String output = objectMapper.writeValueAsString(rootNode.get("output"));

        response.path(path).matchesJsonStrictly(output);
    }
}
