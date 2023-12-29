package com.unitvectory.serviceauthcentral.gcp.repository;

import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
interface JwtBearerRecordMapper {

	JwtBearerRecordMapper INSTANCE = Mappers.getMapper(JwtBearerRecordMapper.class);

	@Mapping(target = "jwksUrl", expression = "java((String) map.get(\"jwksUrl\"))")
	@Mapping(target = "iss", expression = "java((String) map.get(\"iss\"))")
	@Mapping(target = "sub", expression = "java((String) map.get(\"sub\"))")
	@Mapping(target = "aud", expression = "java((String) map.get(\"aud\"))")
	JwtBearerRecord mapToJwtBearerRecord(Map<String, Object> map);
}