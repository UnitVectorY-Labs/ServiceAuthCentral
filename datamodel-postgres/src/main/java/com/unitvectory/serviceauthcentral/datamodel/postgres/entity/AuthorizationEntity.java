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
package com.unitvectory.serviceauthcentral.datamodel.postgres.entity;

import java.util.ArrayList;
import java.util.List;

import com.unitvectory.serviceauthcentral.datamodel.model.Authorization;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Authorization Entity for PostgreSQL
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "authorizations")
public class AuthorizationEntity implements Authorization {

    @Id
    @Column(name = "document_id", nullable = false, length = 64)
    private String documentId;

    @Column(name = "authorization_created")
    private String authorizationCreated;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "audience", nullable = false)
    private String audience;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "authorization_scopes", joinColumns = @JoinColumn(name = "authorization_id"))
    @Column(name = "scope")
    private List<String> authorizedScopes = new ArrayList<>();
}
