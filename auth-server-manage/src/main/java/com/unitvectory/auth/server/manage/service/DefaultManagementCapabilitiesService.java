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
package com.unitvectory.auth.server.manage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.unitvectory.auth.server.manage.dto.ClientManagementCapabilitiesType;
import com.unitvectory.auth.server.manage.dto.ClientType;
import com.unitvectory.auth.server.manage.dto.RequestJwt;
import com.unitvectory.auth.server.manage.dto.ClientManagementCapabilitiesType.ClientManagementCapabilitiesTypeBuilder;

/**
 * The implementation of the Management Capabilities Service
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Service
public class DefaultManagementCapabilitiesService implements ManagementCapabilitiesService {

    @Value("${sac.issuer}")
    private String issuer;

    @Override
    public ClientManagementCapabilitiesType getClientManagementCapabilities(ClientType client,
            RequestJwt jwt) {
        ClientManagementCapabilitiesTypeBuilder builder =
                ClientManagementCapabilitiesType.builder();

        boolean canDeleteClient = true;
        boolean canAddClientSecret = true;
        boolean canDeleteClientSecret = true;
        boolean canAddClientAuthorization = true;
        boolean canAddAuthorization = true;
        boolean canDeleteAuthorization = true;

        // User records are limited in what they can do
        if ("USER".equals(client.getClientType())) {
            canAddClientSecret = false;
            canDeleteClientSecret = false;
            canAddClientAuthorization = false;
            canAddAuthorization = false;
            canDeleteAuthorization = false;
        }

        // The Issuer application is highly limited as well
        if (this.issuer.equals(client.getClientId())) {
            canDeleteClient = false;
            canAddClientSecret = false;
            canDeleteClientSecret = false;
            canAddClientAuthorization = false;
            canAddAuthorization = false;
            canDeleteAuthorization = false;
        }

        // If the subject of the JWT matches the clientId then it can't be deleted
        if (jwt.getSubject().equals(client.getClientId())) {
            canDeleteClient = false;
        }

        builder.canDeleteClient(canDeleteClient);
        builder.canAddClientSecret(canAddClientSecret);
        builder.canDeleteClientSecret(canDeleteClientSecret);
        builder.canAddClientAuthorization(canAddClientAuthorization);
        builder.canAddAuthorization(canAddAuthorization);
        builder.canDeleteAuthorization(canDeleteAuthorization);

        return builder.build();
    }

}
