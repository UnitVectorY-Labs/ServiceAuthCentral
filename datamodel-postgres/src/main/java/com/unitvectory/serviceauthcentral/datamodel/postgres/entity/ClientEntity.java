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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientJwtBearer;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientScope;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Client Entity for PostgreSQL
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clients")
public class ClientEntity implements Client {

    @Id
    @Column(name = "document_id", nullable = false, length = 64)
    private String documentId;

    @Column(name = "client_created")
    private String clientCreated;

    @Column(name = "client_id", nullable = false, unique = true)
    private String clientId;

    @Column(name = "description")
    private String description;

    @Column(name = "salt", nullable = false)
    private String salt;

    @Enumerated(EnumType.STRING)
    @Column(name = "client_type", nullable = false)
    private ClientType clientType;

    @Column(name = "client_secret1")
    private String clientSecret1;

    @Column(name = "client_secret1_updated")
    private String clientSecret1Updated;

    @Column(name = "client_secret2")
    private String clientSecret2;

    @Column(name = "client_secret2_updated")
    private String clientSecret2Updated;

    @Column(name = "locked")
    private Boolean locked;

    @Builder.Default
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ClientScopeEntity> availableScopesEntities = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ClientJwtBearerEntity> jwtBearerEntities = new ArrayList<>();

    @Override
    public List<ClientScope> getAvailableScopes() {
        if (this.availableScopesEntities == null) {
            return Collections.emptyList();
        }

        return availableScopesEntities.stream().map(obj -> (ClientScope) obj)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<ClientJwtBearer> getJwtBearer() {
        if (this.jwtBearerEntities == null) {
            return Collections.emptyList();
        }

        return jwtBearerEntities.stream().map(obj -> (ClientJwtBearer) obj)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
