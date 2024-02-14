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
package com.unitvectory.auth.datamodel.firestore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.unitvectory.auth.datamodel.firestore.model.CachedJwkRecord;
import com.unitvectory.auth.datamodel.model.CachedJwk;

/**
 * The mapper for the CachedJwkRecord
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Mapper
public interface CachedJwkRecordMapper {

	CachedJwkRecordMapper INSTANCE = Mappers.getMapper(CachedJwkRecordMapper.class);

	@Mapping(target = "documentId", ignore = true)
	@Mapping(target = "url", source = "url")
	@Mapping(target = "ttl",
			expression = "java(com.google.cloud.Timestamp.ofTimeSecondsAndNanos(ttl, 0))")
	@Mapping(target = "valid", source = "jwk.valid")
	@Mapping(target = "kid", source = "jwk.kid")
	@Mapping(target = "kty", source = "jwk.kty")
	@Mapping(target = "alg", source = "jwk.alg")
	@Mapping(target = "use", source = "jwk.use")
	@Mapping(target = "n", source = "jwk.n")
	@Mapping(target = "e", source = "jwk.e")
	CachedJwkRecord cachedJwkToCachedJwkRecord(String url, long ttl, CachedJwk jwk);
}
