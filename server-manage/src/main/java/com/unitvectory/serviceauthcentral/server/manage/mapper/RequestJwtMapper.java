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
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.security.oauth2.jwt.Jwt;
import com.unitvectory.serviceauthcentral.server.manage.dto.RequestJwt;

/**
 * The mapper for RequestJwt
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Mapper
public interface RequestJwtMapper {

    RequestJwtMapper INSTANCE = Mappers.getMapper(RequestJwtMapper.class);

    @Mapping(target = "subject", source = "subject")
    @Mapping(target = "scope", source = "jwt", qualifiedByName = "extractScope")
    RequestJwt requestJwt(Jwt jwt);

    @Named("extractScope")
    default String extractScope(Jwt jwt) {
        return jwt.getClaimAsString("scope");
    }
}
