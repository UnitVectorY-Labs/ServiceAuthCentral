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

import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import com.unitvectory.serviceauthcentral.datamodel.model.LoginState;
import com.unitvectory.serviceauthcentral.datamodel.postgres.entity.LoginStateEntity;
import com.unitvectory.serviceauthcentral.datamodel.repository.LoginStateRepository;
import com.unitvectory.serviceauthcentral.util.HashingUtil;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * The PostgreSQL Login State Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@AllArgsConstructor
public class PostgresLoginStateRepository implements LoginStateRepository {

    private final LoginStateJpaRepository loginStateJpaRepository;

    @Override
    @Transactional
    public void saveState(@NonNull String sessionId, @NonNull String clientId,
            @NonNull String redirectUri, @NonNull String primaryState,
            @NonNull String primaryCodeChallenge, @NonNull String secondaryState, long ttl) {

        // Hashing the sessionId, it is sensitive data that we want to keep away from
        // even admins
        String documentId = HashingUtil.sha256(sessionId);

        LoginStateEntity entity = LoginStateEntity.builder()
                .documentId(documentId)
                .clientId(clientId)
                .redirectUri(redirectUri)
                .primaryState(primaryState)
                .primaryCodeChallenge(primaryCodeChallenge)
                .secondaryState(secondaryState)
                .ttl(ttl)
                .build();

        loginStateJpaRepository.save(entity);
    }

    @Override
    public LoginState getState(@NonNull String sessionId) {
        String documentId = HashingUtil.sha256(sessionId);
        Optional<LoginStateEntity> entity = loginStateJpaRepository.findById(documentId);
        return entity.orElse(null);
    }

    @Override
    @Transactional
    public void deleteState(@NonNull String sessionId) {
        String documentId = HashingUtil.sha256(sessionId);
        loginStateJpaRepository.deleteById(documentId);
    }
}
