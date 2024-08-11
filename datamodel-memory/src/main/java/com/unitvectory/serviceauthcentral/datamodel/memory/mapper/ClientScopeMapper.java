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
package com.unitvectory.serviceauthcentral.datamodel.memory.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.unitvectory.serviceauthcentral.datamodel.memory.model.MemoryClientScope;
import com.unitvectory.serviceauthcentral.datamodel.model.ClientScope;

/**
 * The mapper for CachedJwkRecord
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Mapper
public interface ClientScopeMapper {

    ClientScopeMapper INSTANCE = Mappers.getMapper(ClientScopeMapper.class);

    @Mapping(target = "scope", source = "scope")
    @Mapping(target = "description", source = "description")
    MemoryClientScope clientScopeToMemoryClientScope(ClientScope clientScope);

    List<MemoryClientScope> clientScopeToMemoryClientScope(List<ClientScope> clientScopes);
}
