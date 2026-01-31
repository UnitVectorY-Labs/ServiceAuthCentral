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

import com.unitvectory.serviceauthcentral.datamodel.model.LoginCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Login Code Entity for PostgreSQL
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "login_codes")
public class LoginCodeEntity implements LoginCode {

    @Id
    @Column(name = "document_id", nullable = false, length = 64)
    private String documentId;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "redirect_uri", nullable = false)
    private String redirectUri;

    @Column(name = "code_challenge", nullable = false)
    private String codeChallenge;

    @Column(name = "user_client_id", nullable = false)
    private String userClientId;

    @Column(name = "ttl", nullable = false)
    private Long ttl;

    @Override
    public long getTimeToLive() {
        if (this.ttl != null) {
            return this.ttl;
        } else {
            return 0;
        }
    }
}
