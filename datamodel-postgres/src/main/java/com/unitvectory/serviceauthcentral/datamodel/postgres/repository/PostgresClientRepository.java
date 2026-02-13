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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.unitvectory.consistgen.epoch.EpochTimeProvider;
import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientJwtBearer;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientScope;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientSummary;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientSummaryConnection;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientSummaryEdge;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientType;
import com.unitvectory.serviceauthcentral.datamodel.model.PageInfo;
import com.unitvectory.serviceauthcentral.datamodel.postgres.entity.ClientEntity;
import com.unitvectory.serviceauthcentral.datamodel.postgres.entity.ClientJwtBearerEntity;
import com.unitvectory.serviceauthcentral.datamodel.postgres.entity.ClientScopeEntity;
import com.unitvectory.serviceauthcentral.datamodel.postgres.mapper.ClientSummaryMapper;
import com.unitvectory.serviceauthcentral.datamodel.repository.ClientRepository;
import com.unitvectory.serviceauthcentral.datamodel.time.TimeUtil;
import com.unitvectory.serviceauthcentral.util.HashingUtil;
import com.unitvectory.serviceauthcentral.util.exception.BadRequestException;
import com.unitvectory.serviceauthcentral.util.exception.ConflictException;
import com.unitvectory.serviceauthcentral.util.exception.NotFoundException;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * The PostgreSQL Client Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@AllArgsConstructor
public class PostgresClientRepository implements ClientRepository {

    private final ClientJpaRepository clientJpaRepository;

    private final EpochTimeProvider epochTimeProvider;

    @Override
    public ClientSummaryConnection getClients(Integer first, String after, Integer last, String before) {
        List<ClientSummaryEdge> edges = new ArrayList<>();
        boolean hasNextPage = false;
        boolean hasPreviousPage = false;

        // Forward pagination
        if (first != null) {
            Pageable pageable = PageRequest.of(0, first + 1);
            Page<ClientEntity> page;

            if (after != null && !after.isEmpty()) {
                String afterDecoded = new String(Base64.getDecoder().decode(after), StandardCharsets.UTF_8);
                page = clientJpaRepository.findByClientIdGreaterThanOrderByClientIdAsc(afterDecoded, pageable);
                hasPreviousPage = true; // Since we have an after cursor, there's at least one page before
            } else {
                page = clientJpaRepository.findAllByOrderByClientIdAsc(pageable);
            }

            List<ClientEntity> content = page.getContent();
            hasNextPage = content.size() > first;

            int limit = Math.min(content.size(), first);
            for (int i = 0; i < limit; i++) {
                ClientEntity entity = content.get(i);
                ClientSummary summary = ClientSummaryMapper.INSTANCE.clientEntityToClientSummary(entity);
                String cursor = Base64.getEncoder().encodeToString(entity.getClientId().getBytes(StandardCharsets.UTF_8));
                edges.add(ClientSummaryEdge.builder().cursor(cursor).node(summary).build());
            }
        }
        // Backward pagination
        else if (last != null) {
            Pageable pageable = PageRequest.of(0, last + 1);
            Page<ClientEntity> page;

            if (before != null && !before.isEmpty()) {
                String beforeDecoded = new String(Base64.getDecoder().decode(before), StandardCharsets.UTF_8);
                page = clientJpaRepository.findByClientIdLessThanOrderByClientIdDesc(beforeDecoded, pageable);
                hasNextPage = true; // Since we have a before cursor, there's at least one page after
            } else {
                page = clientJpaRepository.findAllByOrderByClientIdDesc(pageable);
            }

            List<ClientEntity> content = page.getContent();
            hasPreviousPage = content.size() > last;

            // Reverse the list and take only the required items
            List<ClientEntity> reversedContent = new ArrayList<>(content);
            Collections.reverse(reversedContent);
            int startIndex = hasPreviousPage ? 1 : 0;
            int endIndex = reversedContent.size();

            for (int i = startIndex; i < endIndex; i++) {
                ClientEntity entity = reversedContent.get(i);
                ClientSummary summary = ClientSummaryMapper.INSTANCE.clientEntityToClientSummary(entity);
                String cursor = Base64.getEncoder().encodeToString(entity.getClientId().getBytes(StandardCharsets.UTF_8));
                edges.add(ClientSummaryEdge.builder().cursor(cursor).node(summary).build());
            }
        }

        // Determine cursors for pageInfo
        String startCursor = !edges.isEmpty() ? edges.get(0).getCursor() : null;
        String endCursor = !edges.isEmpty() ? edges.get(edges.size() - 1).getCursor() : null;

        PageInfo pageInfo = PageInfo.builder()
                .hasNextPage(hasNextPage)
                .hasPreviousPage(hasPreviousPage)
                .startCursor(startCursor)
                .endCursor(endCursor)
                .build();

        return ClientSummaryConnection.builder().edges(edges).pageInfo(pageInfo).build();
    }

    @Override
    public Client getClient(@NonNull String clientId) {
        String documentId = HashingUtil.sha256(clientId);
        Optional<ClientEntity> entity = clientJpaRepository.findById(documentId);
        return entity.orElse(null);
    }

    @Override
    @Transactional
    public void deleteClient(@NonNull String clientId) {
        String documentId = HashingUtil.sha256(clientId);
        clientJpaRepository.deleteById(documentId);
    }

    @Override
    @Transactional
    public void putClient(@NonNull String clientId, String description, @NonNull String salt,
            @NonNull ClientType clientType, @NonNull List<ClientScope> availableScopes) {

        String documentId = HashingUtil.sha256(clientId);
        String now = TimeUtil.getCurrentTimestamp(this.epochTimeProvider.epochTimeSeconds());

        Optional<ClientEntity> existingEntity = clientJpaRepository.findById(documentId);
        if (existingEntity.isPresent()) {
            throw new ConflictException("clientId already exists");
        }

        ClientEntity entity = ClientEntity.builder()
                .documentId(documentId)
                .clientCreated(now)
                .clientId(clientId)
                .description(description)
                .salt(salt)
                .clientType(clientType)
                .availableScopesEntities(new ArrayList<>())
                .jwtBearerEntities(new ArrayList<>())
                .build();

        // Add available scopes
        for (ClientScope scope : availableScopes) {
            ClientScopeEntity scopeEntity = ClientScopeEntity.builder()
                    .client(entity)
                    .scope(scope.getScope())
                    .description(scope.getDescription())
                    .build();
            entity.getAvailableScopesEntities().add(scopeEntity);
        }

        clientJpaRepository.save(entity);
    }

    @Override
    @Transactional
    public void addClientAvailableScope(@NonNull String clientId, @NonNull ClientScope availableScope) {
        String documentId = HashingUtil.sha256(clientId);

        Optional<ClientEntity> optionalEntity = clientJpaRepository.findById(documentId);
        if (optionalEntity.isEmpty()) {
            throw new NotFoundException("Client not found");
        }

        ClientEntity entity = optionalEntity.get();

        // Check for duplicates
        for (ClientScope scope : entity.getAvailableScopes()) {
            if (scope.getScope().equals(availableScope.getScope())) {
                throw new BadRequestException("Duplicate scope");
            }
        }

        ClientScopeEntity scopeEntity = ClientScopeEntity.builder()
                .client(entity)
                .scope(availableScope.getScope())
                .description(availableScope.getDescription())
                .build();
        entity.getAvailableScopesEntities().add(scopeEntity);

        clientJpaRepository.save(entity);
    }

    @Override
    @Transactional
    public void addAuthorizedJwt(@NonNull String clientId, @NonNull String id,
            @NonNull String jwksUrl, @NonNull String iss, @NonNull String sub,
            @NonNull String aud) {

        String documentId = HashingUtil.sha256(clientId);

        Optional<ClientEntity> optionalEntity = clientJpaRepository.findById(documentId);
        if (optionalEntity.isEmpty()) {
            throw new NotFoundException("Client not found");
        }

        ClientEntity entity = optionalEntity.get();

        // Check for duplicates
        ClientJwtBearerEntity newJwt = ClientJwtBearerEntity.builder()
                .id(id)
                .client(entity)
                .jwksUrl(jwksUrl)
                .iss(iss)
                .sub(sub)
                .aud(aud)
                .build();

        for (ClientJwtBearer cjb : entity.getJwtBearer()) {
            if (newJwt.matches(cjb)) {
                throw new BadRequestException("Duplicate authorization");
            }
        }

        entity.getJwtBearerEntities().add(newJwt);
        clientJpaRepository.save(entity);
    }

    @Override
    @Transactional
    public void removeAuthorizedJwt(@NonNull String clientId, @NonNull String id) {
        String documentId = HashingUtil.sha256(clientId);

        Optional<ClientEntity> optionalEntity = clientJpaRepository.findById(documentId);
        if (optionalEntity.isEmpty()) {
            throw new NotFoundException("Client not found");
        }

        ClientEntity entity = optionalEntity.get();

        ClientJwtBearerEntity jwtToRemove = null;
        for (ClientJwtBearerEntity jwt : entity.getJwtBearerEntities()) {
            if (id.equals(jwt.getId())) {
                jwtToRemove = jwt;
                break;
            }
        }

        if (jwtToRemove != null) {
            entity.getJwtBearerEntities().remove(jwtToRemove);
            clientJpaRepository.save(entity);
        } else {
            throw new NotFoundException("JWT not found with the provided id");
        }
    }

    @Override
    @Transactional
    public void saveClientSecret1(@NonNull String clientId, @NonNull String hashedSecret) {
        String now = TimeUtil.getCurrentTimestamp(this.epochTimeProvider.epochTimeSeconds());
        String documentId = HashingUtil.sha256(clientId);

        Optional<ClientEntity> optionalEntity = clientJpaRepository.findById(documentId);
        if (optionalEntity.isEmpty()) {
            throw new NotFoundException("Client not found");
        }

        ClientEntity entity = optionalEntity.get();
        entity.setClientSecret1(hashedSecret);
        entity.setClientSecret1Updated(now);
        clientJpaRepository.save(entity);
    }

    @Override
    @Transactional
    public void saveClientSecret2(@NonNull String clientId, @NonNull String hashedSecret) {
        String now = TimeUtil.getCurrentTimestamp(this.epochTimeProvider.epochTimeSeconds());
        String documentId = HashingUtil.sha256(clientId);

        Optional<ClientEntity> optionalEntity = clientJpaRepository.findById(documentId);
        if (optionalEntity.isEmpty()) {
            throw new NotFoundException("Client not found");
        }

        ClientEntity entity = optionalEntity.get();
        entity.setClientSecret2(hashedSecret);
        entity.setClientSecret2Updated(now);
        clientJpaRepository.save(entity);
    }

    @Override
    @Transactional
    public void clearClientSecret1(@NonNull String clientId) {
        String now = TimeUtil.getCurrentTimestamp(this.epochTimeProvider.epochTimeSeconds());
        String documentId = HashingUtil.sha256(clientId);

        Optional<ClientEntity> optionalEntity = clientJpaRepository.findById(documentId);
        if (optionalEntity.isEmpty()) {
            throw new NotFoundException("Client not found");
        }

        ClientEntity entity = optionalEntity.get();
        entity.setClientSecret1(null);
        entity.setClientSecret1Updated(now);
        clientJpaRepository.save(entity);
    }

    @Override
    @Transactional
    public void clearClientSecret2(@NonNull String clientId) {
        String now = TimeUtil.getCurrentTimestamp(this.epochTimeProvider.epochTimeSeconds());
        String documentId = HashingUtil.sha256(clientId);

        Optional<ClientEntity> optionalEntity = clientJpaRepository.findById(documentId);
        if (optionalEntity.isEmpty()) {
            throw new NotFoundException("Client not found");
        }

        ClientEntity entity = optionalEntity.get();
        entity.setClientSecret2(null);
        entity.setClientSecret2Updated(now);
        clientJpaRepository.save(entity);
    }
}
