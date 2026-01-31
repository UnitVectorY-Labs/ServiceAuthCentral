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
package com.unitvectory.serviceauthcentral.datamodel.postgres.entity;

import com.unitvectory.serviceauthcentral.datamodel.model.CachedJwk;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Cached JWK Entity for PostgreSQL
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cached_jwks")
public class CachedJwkEntity implements CachedJwk {

    @Id
    @Column(name = "document_id", nullable = false, length = 64)
    private String documentId;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "ttl", nullable = false)
    private Long ttl;

    @Column(name = "valid")
    private boolean valid;

    @Column(name = "kid", nullable = false)
    private String kid;

    @Column(name = "kty")
    private String kty;

    @Column(name = "alg")
    private String alg;

    @Column(name = "use_value")
    private String use;

    @Column(name = "n", columnDefinition = "TEXT")
    private String n;

    @Column(name = "e")
    private String e;

    @Override
    public boolean isExpired(long now) {
        return ttl < now;
    }
}
