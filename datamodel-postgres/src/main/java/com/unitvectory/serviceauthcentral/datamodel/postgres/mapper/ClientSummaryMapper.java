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
package com.unitvectory.serviceauthcentral.datamodel.postgres.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.unitvectory.serviceauthcentral.datamodel.model.ClientSummary;
import com.unitvectory.serviceauthcentral.datamodel.postgres.entity.ClientEntity;

/**
 * The mapper for ClientSummary from ClientEntity
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Mapper
public interface ClientSummaryMapper {

    ClientSummaryMapper INSTANCE = Mappers.getMapper(ClientSummaryMapper.class);

    @Mapping(target = "clientId", source = "clientId")
    @Mapping(target = "description", source = "description")
    ClientSummaryDto clientEntityToClientSummary(ClientEntity entity);

    /**
     * DTO class for ClientSummary
     */
    public class ClientSummaryDto implements ClientSummary {
        private String clientId;
        private String description;

        @Override
        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        @Override
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
