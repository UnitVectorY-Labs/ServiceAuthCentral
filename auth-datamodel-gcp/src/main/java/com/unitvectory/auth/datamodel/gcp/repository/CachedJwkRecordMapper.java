package com.unitvectory.auth.datamodel.gcp.repository;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.unitvectory.auth.datamodel.model.CachedJwk;

@Mapper
interface CachedJwkRecordMapper {

	CachedJwkRecordMapper INSTANCE = Mappers.getMapper(CachedJwkRecordMapper.class);

	@Mapping(target = "documentId", ignore = true)
	@Mapping(target = "url", source = "url")
	@Mapping(target = "ttl", expression = "java(com.google.cloud.Timestamp.ofTimeSecondsAndNanos(ttl, 0))")
	@Mapping(target = "valid", source = "jwk.valid")
	@Mapping(target = "kid", source = "jwk.kid")
	@Mapping(target = "kty", source = "jwk.kty")
	@Mapping(target = "alg", source = "jwk.alg")
	@Mapping(target = "use", source = "jwk.use")
	@Mapping(target = "n", source = "jwk.n")
	@Mapping(target = "e", source = "jwk.e")
	CachedJwkRecord cachedJwkToCachedJwkRecord(String url, long ttl, CachedJwk jwk);
}