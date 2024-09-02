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

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.unitvectory.serviceauthcentral.server.token.dto.OpenIDConfigurationResponse;
import com.unitvectory.serviceauthcentral.server.token.model.OpenIDConfigurationETagResponse;

/**
 * The OpenID Configuration Controller
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@RestController
public class OpenIDConfigurationController {

    @Value("${sac.issuer}")
    private String issuer;

    @GetMapping("/.well-known/openid-configuration")
    public ResponseEntity<OpenIDConfigurationResponse> config(WebRequest request) {
        OpenIDConfigurationETagResponse configETagResponse = getConfig();
        String eTag = configETagResponse.getETag();

        if (request.checkNotModified(eTag)) {
            // Return 304 if not modified, the ETag is the same
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        // Return the response with the ETag
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                // This response can be cached for an hour, goal is to reduce the number of
                // calls to the Sign service
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
                .eTag(eTag)
                .body(configETagResponse.getConfig());
    }

    @Cacheable(value = "openIDConfiguration", key = "openId")
    private OpenIDConfigurationETagResponse getConfig() {
        OpenIDConfigurationResponse response = OpenIDConfigurationResponse.builder()
                // The issuer is the URL of the service
                .issuer(this.issuer)
                // The JWKS URL is derived from the issuer URL
                .jwks_uri(issuer + "/.well-known/jwks.json")
                .build();

        return new OpenIDConfigurationETagResponse(response);
    }
}
