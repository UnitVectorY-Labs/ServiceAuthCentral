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

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.unitvectory.serviceauthcentral.datamodel.model.ClientJwtBearer;
import com.unitvectory.serviceauthcentral.server.manage.dto.ClientJwtBearerType;

/**
 * The mapper for ClientJwtBearerType
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Mapper
public interface ClientJwtBearerMapper {

	ClientJwtBearerMapper INSTANCE = Mappers.getMapper(ClientJwtBearerMapper.class);

	@Mapping(target = "id", source = "id")
	@Mapping(target = "jwksUrl", source = "jwksUrl")
	@Mapping(target = "iss", source = "iss")
	@Mapping(target = "sub", source = "sub")
	@Mapping(target = "aud", source = "aud")
	ClientJwtBearerType mapObj(ClientJwtBearer jwtBearer);

	List<ClientJwtBearerType> mapList(List<ClientJwtBearer> list);
}
