package com.unitvectory.auth.datamodel.memory.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.unitvectory.auth.datamodel.memory.model.MemoryClient;
import com.unitvectory.auth.datamodel.memory.model.MemoryClientSummary;

@Mapper
public interface MemoryClientSummaryMapper {

	MemoryClientSummaryMapper INSTANCE = Mappers.getMapper(MemoryClientSummaryMapper.class);

	@Mapping(target = "clientId", source = "clientId")
	@Mapping(target = "description", source = "description")
	MemoryClientSummary memoryClientToMemoryClientSummary(MemoryClient client);
}
