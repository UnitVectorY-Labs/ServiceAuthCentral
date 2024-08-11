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
package com.unitvectory.serviceauthcentral.server.manage.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.server.manage.dto.ClientType;

/**
 * The mapper for ClientType
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Mapper(uses = ClientJwtBearerMapper.class)
public interface ClientMapper {

	ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

	@Mapping(target = "clientId", source = "clientId")
	@Mapping(target = "clientCreated", source = "clientCreated")
	@Mapping(target = "description", source = "description")
	@Mapping(target = "clientType", source = "clientType")
	@Mapping(target = "clientSecret1Set", expression = "java(client.getClientSecret1() != null)")
	@Mapping(target = "clientSecret1Updated", source = "clientSecret1Updated")
	@Mapping(target = "clientSecret2Set", expression = "java(client.getClientSecret2() != null)")
	@Mapping(target = "clientSecret2Updated", source = "clientSecret2Updated")
	@Mapping(target = "jwtBearer", source = "jwtBearer")
	ClientType clientToClientType(Client client);
}
