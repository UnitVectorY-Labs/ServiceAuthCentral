package com.unitvectory.auth.datamodel.gcp.repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.google.cloud.firestore.DocumentSnapshot;

@Mapper
interface ClientRecordMapper {

	ClientRecordMapper INSTANCE = Mappers.getMapper(ClientRecordMapper.class);

	@Mapping(target = "documentId", expression = "java(doc.getId())")
	@Mapping(target = "clientId", expression = "java(doc.getString(\"clientId\"))")
	@Mapping(target = "description", expression = "java(doc.getString(\"description\"))")
	@Mapping(target = "salt", expression = "java(doc.getString(\"salt\"))")
	@Mapping(target = "clientSecret1", expression = "java(doc.getString(\"clientSecret1\"))")
	@Mapping(target = "clientSecret2", expression = "java(doc.getString(\"clientSecret2\"))")
	@Mapping(target = "jwtBearer", expression = "java(mapJwtBearerList(doc))")
	ClientRecord documentSnapshotToClientRecord(DocumentSnapshot doc);

	default List<JwtBearerRecord> mapJwtBearerList(DocumentSnapshot doc) {
		Object jwtBearerObject = doc.get("jwtBearer");
		if (jwtBearerObject instanceof List) {
			List<?> jwtBearerList = (List<?>) jwtBearerObject;
			return jwtBearerList.stream().filter(Map.class::isInstance).map(Map.class::cast)
					.map(JwtBearerRecordMapper.INSTANCE::mapToJwtBearerRecord).collect(Collectors.toList());
		} else {
			return Collections.emptyList(); // Or null, based on your preference
		}
	}
}