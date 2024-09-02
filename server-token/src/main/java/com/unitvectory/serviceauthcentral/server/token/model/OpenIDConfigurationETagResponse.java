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

import com.unitvectory.serviceauthcentral.server.token.dto.OpenIDConfigurationResponse;
import com.unitvectory.serviceauthcentral.util.HashingUtil;

import lombok.Getter;

/**
 * The Cached OpenID Configuration
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Getter
public class OpenIDConfigurationETagResponse {

    private final OpenIDConfigurationResponse config;

    private final String eTag;

    public OpenIDConfigurationETagResponse(OpenIDConfigurationResponse config) {
        this.config = config;

        // Not a complex eTag calculation, this object doesn't really change
        this.eTag = HashingUtil.sha256("Issuer:" + config.getIssuer());
    }
}
