package com.unitvectory.auth.verify.auth0.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.auth0.jwk.Jwk;
import com.unitvectory.auth.verify.model.VerifyJwk;

@Mapper
public interface JwkAuth0Mapper {

	JwkAuth0Mapper INSTANCE = Mappers.getMapper(JwkAuth0Mapper.class);

	@Mapping(target = "kty", source = "jwk.type")
	@Mapping(target = "use", source = "jwk.usage")
	@Mapping(target = "kid", source = "jwk.id")
	@Mapping(target = "alg", source = "jwk.algorithm")
	@Mapping(target = "n", expression = "java((String)jwk.getAdditionalAttributes().get(\"n\"))")
	@Mapping(target = "e", expression = "java((String)jwk.getAdditionalAttributes().get(\"e\"))")
	VerifyJwk convert(Jwk jwk);
}
