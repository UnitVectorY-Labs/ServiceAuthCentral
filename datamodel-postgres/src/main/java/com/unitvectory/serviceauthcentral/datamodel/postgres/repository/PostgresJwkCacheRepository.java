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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com.unitvectory.serviceauthcentral.datamodel.model.CachedJwk;
import com.unitvectory.serviceauthcentral.datamodel.postgres.entity.CachedJwkEntity;
import com.unitvectory.serviceauthcentral.datamodel.repository.JwkCacheRepository;
import com.unitvectory.serviceauthcentral.util.HashingUtil;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * The PostgreSQL JWK Cache Repository
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@AllArgsConstructor
public class PostgresJwkCacheRepository implements JwkCacheRepository {

    private final CachedJwkJpaRepository cachedJwkJpaRepository;

    private String getId(@NonNull String url, @NonNull String id) {
        String urlHash = HashingUtil.sha256(url);
        String idHash = HashingUtil.sha256(id);
        return HashingUtil.sha256(urlHash + idHash);
    }

    @Override
    @Transactional
    public void cacheJwk(@NonNull String url, @NonNull CachedJwk jwk, long ttl) {
        String documentId = getId(url, jwk.getKid());

        CachedJwkEntity entity = CachedJwkEntity.builder()
                .documentId(documentId)
                .url(url)
                .ttl(ttl)
                .valid(jwk.isValid())
                .kid(jwk.getKid())
                .kty(jwk.getKty())
                .alg(jwk.getAlg())
                .use(jwk.getUse())
                .n(jwk.getN())
                .e(jwk.getE())
                .build();

        cachedJwkJpaRepository.save(entity);
    }

    @Override
    @Transactional
    public void cacheJwkAbsent(@NonNull String url, @NonNull String kid, long ttl) {
        String documentId = getId(url, kid);

        CachedJwkEntity entity = CachedJwkEntity.builder()
                .documentId(documentId)
                .url(url)
                .kid(kid)
                .ttl(ttl)
                .valid(false)
                .build();

        cachedJwkJpaRepository.save(entity);
    }

    @Override
    public List<CachedJwk> getJwks(@NonNull String url) {
        List<CachedJwkEntity> entities = cachedJwkJpaRepository.findByUrl(url);
        return Collections.unmodifiableList(
                entities.stream().map(e -> (CachedJwk) e).collect(Collectors.toList()));
    }

    @Override
    public CachedJwk getJwk(String url, String kid) {
        String id = getId(url, kid);
        Optional<CachedJwkEntity> entity = cachedJwkJpaRepository.findById(id);
        return entity.orElse(null);
    }
}
