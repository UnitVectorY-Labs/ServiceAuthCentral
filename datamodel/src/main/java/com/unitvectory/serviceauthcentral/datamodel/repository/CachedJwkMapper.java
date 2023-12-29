package com.unitvectory.serviceauthcentral.datamodel.repository;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.unitvectory.serviceauthcentral.datamodel.model.CachedJwk;

@Mapper
interface CachedJwkMapper {

	CachedJwkMapper INSTANCE = Mappers.getMapper(CachedJwkMapper.class);

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