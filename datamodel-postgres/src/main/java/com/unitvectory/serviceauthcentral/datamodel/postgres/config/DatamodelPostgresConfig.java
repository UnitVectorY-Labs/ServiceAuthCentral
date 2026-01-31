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
package com.unitvectory.serviceauthcentral.datamodel.postgres.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.unitvectory.consistgen.epoch.EpochTimeProvider;
import com.unitvectory.serviceauthcentral.datamodel.postgres.repository.AuthorizationJpaRepository;
import com.unitvectory.serviceauthcentral.datamodel.postgres.repository.CachedJwkJpaRepository;
import com.unitvectory.serviceauthcentral.datamodel.postgres.repository.ClientJpaRepository;
import com.unitvectory.serviceauthcentral.datamodel.postgres.repository.LoginCodeJpaRepository;
import com.unitvectory.serviceauthcentral.datamodel.postgres.repository.LoginStateJpaRepository;
import com.unitvectory.serviceauthcentral.datamodel.postgres.repository.PostgresAuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.postgres.repository.PostgresClientRepository;
import com.unitvectory.serviceauthcentral.datamodel.postgres.repository.PostgresJwkCacheRepository;
import com.unitvectory.serviceauthcentral.datamodel.postgres.repository.PostgresLoginCodeRepository;
import com.unitvectory.serviceauthcentral.datamodel.postgres.repository.PostgresLoginStateRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.JwkCacheRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.LoginCodeRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.LoginStateRepository;

/**
 * The data model config for PostgreSQL
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Configuration
@Profile("datamodel-postgres")
@EntityScan(basePackages = "com.unitvectory.serviceauthcentral.datamodel.postgres.entity")
@EnableJpaRepositories(basePackages = "com.unitvectory.serviceauthcentral.datamodel.postgres.repository")
public class DatamodelPostgresConfig {

    @Autowired
    private EpochTimeProvider epochTimeProvider;

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
    public AuthorizationRepository authorizationRepository() {
        return new PostgresAuthorizationRepository(this.authorizationJpaRepository, this.epochTimeProvider);
    }

    @Bean
    public ClientRepository clientRepository() {
        return new PostgresClientRepository(this.clientJpaRepository, this.epochTimeProvider);
    }

    @Bean
    public JwkCacheRepository jwkCacheRepository() {
        return new PostgresJwkCacheRepository(this.cachedJwkJpaRepository);
    }

    @Bean
    public LoginCodeRepository loginCodeRepository() {
        return new PostgresLoginCodeRepository(this.loginCodeJpaRepository);
    }

    @Bean
    public LoginStateRepository loginStateRepository() {
        return new PostgresLoginStateRepository(this.loginStateJpaRepository);
    }
}
