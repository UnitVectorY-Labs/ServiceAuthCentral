package com.unitvectory.auth.datamodel.gcp.mapper;

import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.unitvectory.auth.datamodel.gcp.model.JwtBearerRecord;

@Mapper
public interface JwtBearerRecordMapper {

	JwtBearerRecordMapper INSTANCE = Mappers.getMapper(JwtBearerRecordMapper.class);

	@Mapping(target = "jwksUrl", expression = "java((String) map.get(\"jwksUrl\"))")
	@Mapping(target = "iss", expression = "java((String) map.get(\"iss\"))")
	@Mapping(target = "sub", expression = "java((String) map.get(\"sub\"))")
	@Mapping(target = "aud", expression = "java((String) map.get(\"aud\"))")
	JwtBearerRecord mapToJwtBearerRecord(Map<String, Object> map);
}