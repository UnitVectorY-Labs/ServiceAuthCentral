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
package com.unitvectory.serviceauthcentral.datamodel.memory.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.unitvectory.consistgen.epoch.EpochTimeProvider;
import com.unitvectory.serviceauthcentral.datamodel.memory.repository.MemoryAuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.memory.repository.MemoryClientRepository;
import com.unitvectory.serviceauthcentral.datamodel.memory.repository.MemoryJwkCacheRepository;
import com.unitvectory.serviceauthcentral.datamodel.memory.repository.MemoryLoginCodeRepository;
import com.unitvectory.serviceauthcentral.datamodel.memory.repository.MemoryLoginStateRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.JwkCacheRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.LoginCodeRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.LoginStateRepository;

@Configuration
@Profile("datamodel-memory")
public class MemoryConfig {

    @Autowired
	private EpochTimeProvider epochTimeProvider;

    @Bean
    public AuthorizationRepository authorizationRepository() {
        return new MemoryAuthorizationRepository(this.epochTimeProvider);
    }

    @Bean
    public ClientRepository clientRepository() {
        return new MemoryClientRepository(this.epochTimeProvider);
    }

    @Bean
    public JwkCacheRepository jwkCacheRepository() {
        return new MemoryJwkCacheRepository();
    }

    @Bean
    public LoginCodeRepository loginCodeRepository() {
        return new MemoryLoginCodeRepository();
    }

    @Bean
    public LoginStateRepository loginStateRepository() {
        return new MemoryLoginStateRepository();
    }
}
