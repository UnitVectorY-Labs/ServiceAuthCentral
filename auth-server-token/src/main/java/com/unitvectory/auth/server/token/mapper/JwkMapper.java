package com.unitvectory.auth.server.token.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.unitvectory.auth.datamodel.model.CachedJwk;
import com.unitvectory.auth.server.token.dto.JwkResponse;
import com.unitvectory.auth.server.token.model.CachedJwkRecord;
import com.unitvectory.auth.sign.model.SignJwk;
import com.unitvectory.auth.verify.model.VerifyJwk;

@Mapper
public interface JwkMapper {

	JwkMapper INSTANCE = Mappers.getMapper(JwkMapper.class);

	@Mapping(target = "kid", source = "jwk.kid")
	@Mapping(target = "kty", source = "jwk.kty")
	@Mapping(target = "alg", source = "jwk.alg")
	@Mapping(target = "use", source = "jwk.use")
	@Mapping(target = "n", source = "jwk.n")
	@Mapping(target = "e", source = "jwk.e")
	JwkResponse signJwkToJwkResponse(SignJwk jwk);

	@Mapping(target = "kid", source = "jwk.kid")
	@Mapping(target = "kty", source = "jwk.kty")
	@Mapping(target = "alg", source = "jwk.alg")
	@Mapping(target = "use", source = "jwk.use")
	@Mapping(target = "n", source = "jwk.n")
	@Mapping(target = "e", source = "jwk.e")
	VerifyJwk cachedJwkToVerifyJwk(CachedJwk jwk);

	@Mapping(target = "kid", source = "jwk.kid")
	@Mapping(target = "kty", source = "jwk.kty")
	@Mapping(target = "alg", source = "jwk.alg")
	@Mapping(target = "use", source = "jwk.use")
	@Mapping(target = "n", source = "jwk.n")
	@Mapping(target = "e", source = "jwk.e")
	CachedJwkRecord verifyJwkToCachedJwk(VerifyJwk jwk);

}
