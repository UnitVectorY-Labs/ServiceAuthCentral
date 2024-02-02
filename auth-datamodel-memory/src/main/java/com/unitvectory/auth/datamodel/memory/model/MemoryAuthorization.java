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
package com.unitvectory.auth.datamodel.memory.model;

import com.unitvectory.auth.datamodel.model.Authorization;
import com.unitvectory.auth.util.HashingUtil;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Value
@Builder
public class MemoryAuthorization implements Authorization {

	private String subject;

	private String audience;

	public boolean matches(@NonNull String subject, @NonNull String audience) {
		return subject.equals(this.subject) && audience.equals(this.audience);
	}

	@Override
	public String getDocumentId() {
		String subjectHash = HashingUtil.sha256(subject);
		String audienceHash = HashingUtil.sha256(audience);
		return HashingUtil.sha256(subjectHash + audienceHash);
	}
}
