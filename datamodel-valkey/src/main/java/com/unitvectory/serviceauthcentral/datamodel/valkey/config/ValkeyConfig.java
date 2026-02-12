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
package com.unitvectory.serviceauthcentral.datamodel.valkey.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.unitvectory.consistgen.epoch.EpochTimeProvider;
import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.JwkCacheRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.LoginCodeRepository;
import com.unitvectory.serviceauthcentral.datamodel.repository.LoginStateRepository;
import com.unitvectory.serviceauthcentral.datamodel.valkey.repository.ValkeyAuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.valkey.repository.ValkeyClientRepository;
import com.unitvectory.serviceauthcentral.datamodel.valkey.repository.ValkeyJwkCacheRepository;
import com.unitvectory.serviceauthcentral.datamodel.valkey.repository.ValkeyLoginCodeRepository;
import com.unitvectory.serviceauthcentral.datamodel.valkey.repository.ValkeyLoginStateRepository;

@Configuration
@Profile("datamodel-valkey")
public class ValkeyConfig {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private EpochTimeProvider epochTimeProvider;

    @Bean
    public AuthorizationRepository authorizationRepository() {
        return new ValkeyAuthorizationRepository(stringRedisTemplate, epochTimeProvider);
    }

    @Bean
    public ClientRepository clientRepository() {
        return new ValkeyClientRepository(stringRedisTemplate, epochTimeProvider);
    }

    @Bean
    public JwkCacheRepository jwkCacheRepository() {
        return new ValkeyJwkCacheRepository(stringRedisTemplate);
    }

    @Bean
    public LoginCodeRepository loginCodeRepository() {
        return new ValkeyLoginCodeRepository(stringRedisTemplate);
    }

    @Bean
    public LoginStateRepository loginStateRepository() {
        return new ValkeyLoginStateRepository(stringRedisTemplate);
    }
}
