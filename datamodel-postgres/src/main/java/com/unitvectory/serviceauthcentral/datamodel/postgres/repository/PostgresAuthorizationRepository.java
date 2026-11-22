/*
 * Copyright 2026 the original author or authors.
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import com.unitvectory.consistgen.epoch.EpochTimeProvider;
import com.unitvectory.serviceauthcentral.datamodel.model.Authorization;
import com.unitvectory.serviceauthcentral.datamodel.postgres.entity.AuthorizationEntity;
import com.unitvectory.serviceauthcentral.datamodel.repository.AuthorizationRepository;
import com.unitvectory.serviceauthcentral.datamodel.time.TimeUtil;
import com.unitvectory.serviceauthcentral.util.HashingUtil;
import com.unitvectory.serviceauthcentral.util.exception.BadRequestException;
import com.unitvectory.serviceauthcentral.util.exception.NotFoundException;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * The PostgreSQL Authorization Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@AllArgsConstructor
public class PostgresAuthorizationRepository implements AuthorizationRepository {

    private final AuthorizationJpaRepository authorizationJpaRepository;

    private final EpochTimeProvider epochTimeProvider;

    @Override
    public Authorization getAuthorization(@NonNull String id) {
        Optional<AuthorizationEntity> entity = authorizationJpaRepository.findById(id);
        return entity.orElse(null);
    }

    @Override
    @Transactional
    public void deleteAuthorization(@NonNull String id) {
        authorizationJpaRepository.deleteById(id);
    }

    @Override
    public Authorization getAuthorization(@NonNull String subject, @NonNull String audience) {
        Optional<AuthorizationEntity> entity = authorizationJpaRepository.findBySubjectAndAudience(subject, audience);
        return entity.orElse(null);
    }

    @Override
    public Iterator<Authorization> getAuthorizationBySubject(@NonNull String subject) {
        List<AuthorizationEntity> entities = authorizationJpaRepository.findBySubject(subject);
        List<Authorization> list = new ArrayList<>();
        for (AuthorizationEntity entity : entities) {
            list.add(entity);
        }
        return list.iterator();
    }

    @Override
    public Iterator<Authorization> getAuthorizationByAudience(@NonNull String audience) {
        List<AuthorizationEntity> entities = authorizationJpaRepository.findByAudience(audience);
        List<Authorization> list = new ArrayList<>();
        for (AuthorizationEntity entity : entities) {
            list.add(entity);
        }
        return list.iterator();
    }

    @Override
    @Transactional
    public void authorize(@NonNull String subject, @NonNull String audience,
            @NonNull List<String> authorizedScopes) {
        String now = TimeUtil.getCurrentTimestamp(this.epochTimeProvider.epochTimeSeconds());
        String documentId = getDocumentId(subject, audience);
        
        AuthorizationEntity entity = AuthorizationEntity.builder()
                .documentId(documentId)
                .authorizationCreated(now)
                .subject(subject)
                .audience(audience)
                .authorizedScopes(new ArrayList<>(authorizedScopes))
                .build();
        
        authorizationJpaRepository.save(entity);
    }

    @Override
    @Transactional
    public void deauthorize(@NonNull String subject, @NonNull String audience) {
        String documentId = getDocumentId(subject, audience);
        authorizationJpaRepository.deleteById(documentId);
    }

    @Override
    @Transactional
    public void authorizeAddScope(@NonNull String subject, @NonNull String audience,
            @NonNull String authorizedScope) {
        String documentId = getDocumentId(subject, audience);
        
        Optional<AuthorizationEntity> optionalEntity = authorizationJpaRepository.findById(documentId);
        if (optionalEntity.isEmpty()) {
            throw new NotFoundException("Client not found");
        }
        
        AuthorizationEntity entity = optionalEntity.get();
        
        if (entity.getAuthorizedScopes().contains(authorizedScope)) {
            throw new BadRequestException("Scope already authorized");
        }
        
        entity.getAuthorizedScopes().add(authorizedScope);
        authorizationJpaRepository.save(entity);
    }

    @Override
    @Transactional
    public void authorizeRemoveScope(@NonNull String subject, @NonNull String audience,
            @NonNull String authorizedScope) {
        String documentId = getDocumentId(subject, audience);
        
        Optional<AuthorizationEntity> optionalEntity = authorizationJpaRepository.findById(documentId);
        if (optionalEntity.isEmpty()) {
            throw new NotFoundException("Client not found");
        }
        
        AuthorizationEntity entity = optionalEntity.get();
        entity.getAuthorizedScopes().remove(authorizedScope);
        authorizationJpaRepository.save(entity);
    }

    private String getDocumentId(@NonNull String subject, @NonNull String audience) {
        String subjectHash = HashingUtil.sha256(subject);
        String audienceHash = HashingUtil.sha256(audience);
        return HashingUtil.sha256(subjectHash + audienceHash);
    }
}
