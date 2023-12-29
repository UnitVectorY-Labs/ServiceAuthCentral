package com.unitvectory.auth.datamodel.repository.memory;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.unitvectory.auth.datamodel.model.CachedJwk;

@Mapper
interface MemoryCachedJwkMapper {

	MemoryCachedJwkMapper INSTANCE = Mappers.getMapper(MemoryCachedJwkMapper.class);

	@Mapping(target = "url", source = "url")
	@Mapping(target = "ttl", source = "ttl")
	@Mapping(target = "valid", source = "jwk.valid")
	@Mapping(target = "kid", source = "jwk.kid")
	@Mapping(target = "kty", source = "jwk.kty")
	@Mapping(target = "alg", source = "jwk.alg")
	@Mapping(target = "use", source = "jwk.use")
	@Mapping(target = "n", source = "jwk.n")
	@Mapping(target = "e", source = "jwk.e")
	MemoryCachedJwk cachedJwkToMemoryCachedJwk(String url, long ttl, CachedJwk jwk);
}