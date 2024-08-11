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

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import com.unitvectory.fileparamunit.ListFileSource;

/**
 * The ClientResolver test.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@SpringBootTest(properties = {"graphql.servlet.enabled=false",
        "graphql.servlet.websocket.enabled=false", "sac.issuer=https://api.example.com",
        "sac.cors.origins=https://console.example.com"})
@AutoConfigureHttpGraphQlTester
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles({"datamodel-memory", "test"})
public class ClientResolverTest extends AbstractResolverTest {

    @Autowired
    HttpGraphQlTester httpGraphQlTester;

    @ParameterizedTest
    @ListFileSource(resources = "/ClientResolver/", fileExtension = ".json", recurse = true)
    public void exampleTest(String fileName) {
        this.runTest(httpGraphQlTester, fileName);
    }
}
