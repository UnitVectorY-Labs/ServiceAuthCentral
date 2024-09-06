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
package com.unitvectory.serviceauthcentral.server.manage.dto;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Value;

/**
 * The Request JWT
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Value
@Builder
public class RequestJwt {

    /**
     * The subject claim
     */
    private final String subject;

    /**
     * The scope claim
     */
    private final String scope;

    /**
     * Get the scopes as a set
     * 
     * @return the scopes
     */
    public Set<String> getScopesSet() {
        if (this.scope == null) {
            return Set.of();
        }

        return Arrays.stream(scope.split(" ")).collect(Collectors.toSet());
    }

    public boolean isReadAuthorized() {
        // Scopes contains "Read" or "Admin"
        return this.getScopesSet().contains("Read") || this.getScopesSet().contains("Admin");
    }

    public boolean isWriteAuthorized() {
        // Scopes contains "Admin"
        return this.getScopesSet().contains("Admin");
    }
}
