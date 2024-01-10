package com.unitvectory.auth.server.manage.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.unitvectory.auth.datamodel.model.ClientJwtBearer;
import com.unitvectory.auth.server.manage.dto.ClientJwtBearerType;

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
