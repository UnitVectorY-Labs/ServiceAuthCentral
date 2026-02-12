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
package com.unitvectory.serviceauthcentral.datamodel.postgres.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.unitvectory.consistgen.epoch.EpochTimeProvider;
import com.unitvectory.consistgen.epoch.SystemEpochTimeProvider;
import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.JwkCacheRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.LoginCodeRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.LoginStateRepository;

/**
 * Test configuration for PostgreSQL integration tests
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@SpringBootApplication
@EntityScan(basePackages = "com.unitvectory.serviceauthcentral.datamodel.postgres.entity")
@EnableJpaRepositories(basePackages = "com.unitvectory.serviceauthcentral.datamodel.postgres.repository")
public class TestConfig {

    @Bean
    public EpochTimeProvider epochTimeProvider() {
        return SystemEpochTimeProvider.getInstance();
    }

    @Autowired
    private AuthorizationJpaRepository authorizationJpaRepository;

    @Autowired
    private ClientJpaRepository clientJpaRepository;

    @Autowired
    private CachedJwkJpaRepository cachedJwkJpaRepository;

    @Autowired
    private LoginCodeJpaRepository loginCodeJpaRepository;

    @Autowired
    private LoginStateJpaRepository loginStateJpaRepository;

    @Bean
    public AuthorizationRepository authorizationRepository(EpochTimeProvider epochTimeProvider) {
        return new PostgresAuthorizationRepository(authorizationJpaRepository, epochTimeProvider);
    }

    @Bean
    public ClientRepository clientRepository(EpochTimeProvider epochTimeProvider) {
        return new PostgresClientRepository(clientJpaRepository, epochTimeProvider);
    }

    @Bean
    public JwkCacheRepository jwkCacheRepository() {
        return new PostgresJwkCacheRepository(cachedJwkJpaRepository);
    }

    @Bean
    public LoginCodeRepository loginCodeRepository() {
        return new PostgresLoginCodeRepository(loginCodeJpaRepository);
    }

    @Bean
    public LoginStateRepository loginStateRepository() {
        return new PostgresLoginStateRepository(loginStateJpaRepository);
    }
}
