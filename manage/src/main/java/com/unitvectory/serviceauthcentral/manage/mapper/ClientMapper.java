package com.unitvectory.serviceauthcentral.manage.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.unitvectory.serviceauthcentral.datamodel.model.Client;
import com.unitvectory.serviceauthcentral.manage.dto.ClientType;

@Mapper
public interface ClientMapper {

	ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

	@Mapping(target = "clientId", source = "clientId")
	@Mapping(target = "clientSecret1Set", expression = "java(client.getClientSecret1() != null)")
	@Mapping(target = "clientSecret2Set", expression = "java(client.getClientSecret2() != null)")
	ClientType clientToClientType(Client client);
}