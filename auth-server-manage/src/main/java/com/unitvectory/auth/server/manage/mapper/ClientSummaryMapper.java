package com.unitvectory.auth.server.manage.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.unitvectory.auth.datamodel.model.ClientSummary;
import com.unitvectory.auth.server.manage.dto.ClientSummaryType;

@Mapper()
public interface ClientSummaryMapper {

	ClientSummaryMapper INSTANCE = Mappers.getMapper(ClientSummaryMapper.class);

	@Mapping(target = "clientId", source = "clientId")
	@Mapping(target = "description", source = "description")
	ClientSummaryType clientSummaryToClientSummaryType(ClientSummary client);
}
