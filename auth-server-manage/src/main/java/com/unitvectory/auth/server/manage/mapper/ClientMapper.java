package com.unitvectory.auth.server.manage.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.unitvectory.auth.datamodel.model.Client;
import com.unitvectory.auth.server.manage.dto.ClientType;

@Mapper(uses = ClientJwtBearerMapper.class)
public interface ClientMapper {

	ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

	@Mapping(target = "clientId", source = "clientId")
	@Mapping(target = "description", source = "description")
	@Mapping(target = "clientSecret1Set", expression = "java(client.getClientSecret1() != null)")
	@Mapping(target = "clientSecret2Set", expression = "java(client.getClientSecret2() != null)")
	@Mapping(target = "jwtBearer", source = "jwtBearer")
	ClientType clientToClientType(Client client);
}
