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
package com.unitvectory.serviceauthcentral.server.token.model;

import com.unitvectory.serviceauthcentral.server.token.dto.JwkResponse;
import com.unitvectory.serviceauthcentral.server.token.dto.JwksResponse;
import com.unitvectory.serviceauthcentral.util.HashingUtil;

import lombok.Getter;

/**
 * The Cached JWK Records
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Getter
public class JwkETagResponse {

    private final JwksResponse jwks;

    private final String eTag;

    public JwkETagResponse(JwksResponse jwks) {
        this.jwks = jwks;

        // Calculate the eTag based on the keys
        StringBuilder sb = new StringBuilder();
        for (JwkResponse key : this.jwks.getKeys()) {
            // Concatenate all of the KIDs which are the unique key identifiers, if a KID is
            // added or removed the ETag will change
            sb.append(key.getKid());
            sb.append("\n");
        }

        // Calculate the SHA-256 hash of the concatenated KIDs to get a unique ETag
        this.eTag = HashingUtil.sha256(sb.toString());
    }
}
