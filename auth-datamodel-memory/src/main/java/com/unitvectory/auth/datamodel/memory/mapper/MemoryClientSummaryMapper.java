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
package com.unitvectory.auth.datamodel.memory.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.unitvectory.auth.datamodel.memory.model.MemoryClient;
import com.unitvectory.auth.datamodel.memory.model.MemoryClientSummary;

/**
 * The mapper for the MemoryClientSummary
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Mapper
public interface MemoryClientSummaryMapper {

	MemoryClientSummaryMapper INSTANCE = Mappers.getMapper(MemoryClientSummaryMapper.class);

	@Mapping(target = "clientId", source = "clientId")
	@Mapping(target = "description", source = "description")
	MemoryClientSummary memoryClientToMemoryClientSummary(MemoryClient client);
}
