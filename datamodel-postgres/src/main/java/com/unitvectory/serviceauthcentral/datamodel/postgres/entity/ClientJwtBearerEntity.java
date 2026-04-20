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

import com.unitvectory.serviceauthcentral.datamodel.model.ClientJwtBearer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Client JWT Bearer Entity for PostgreSQL
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "client_jwt_bearers")
public class ClientJwtBearerEntity implements ClientJwtBearer {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @Column(name = "jwks_url", nullable = false)
    private String jwksUrl;

    @Column(name = "iss", nullable = false)
    private String iss;

    @Column(name = "sub", nullable = false)
    private String sub;

    @Column(name = "aud", nullable = false)
    private String aud;
}
