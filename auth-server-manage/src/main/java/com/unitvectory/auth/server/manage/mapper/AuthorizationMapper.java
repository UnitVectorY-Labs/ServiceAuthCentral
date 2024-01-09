package com.unitvectory.auth.server.manage.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.unitvectory.auth.datamodel.model.Authorization;
import com.unitvectory.auth.server.manage.dto.AuthorizationType;

@Mapper
public interface AuthorizationMapper {

	AuthorizationMapper INSTANCE = Mappers.getMapper(AuthorizationMapper.class);

	@Mapping(target = "id", source = "documentId")
	@Mapping(target = "subjectId", source = "subject")
	@Mapping(target = "audienceId", source = "audience")
	AuthorizationType authorizationToAuthorizationType(Authorization authorization);
}
