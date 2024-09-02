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
package com.unitvectory.serviceauthcentral.server.token.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unitvectory.serviceauthcentral.server.token.dto.OpenIDConfigurationResponse;

/**
 * The OpenID Configuration Controller
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@RestController
public class OpenIDConfigurationController {

    @Value("${sac.issuer}")
    private String issuer;

    @Cacheable(value = "openIDConfiguration", key = "'openId'")
    @GetMapping("/.well-known/openid-configuration")
    public OpenIDConfigurationResponse config() {
        // Returning a minimal configuration
        return OpenIDConfigurationResponse.builder()
                // The issuer is the URL of the service
                .issuer(this.issuer)
                // The JWKS URL is derived from the issuer URL
                .jwks_uri(issuer + "/.well-known/jwks.json")
                .build();
    }
}
