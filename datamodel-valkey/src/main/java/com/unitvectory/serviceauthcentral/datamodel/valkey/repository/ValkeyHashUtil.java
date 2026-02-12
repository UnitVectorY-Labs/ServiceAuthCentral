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
package com.unitvectory.serviceauthcentral.datamodel.valkey.repository;

import java.util.Map;

/**
 * Shared utility methods for Valkey repository hash operations.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
final class ValkeyHashUtil {

	private ValkeyHashUtil() {
	}

	static String getStr(Map<Object, Object> entries, String field) {
		Object val = entries.get(field);
		if (val == null) {
			return null;
		}
		String str = val.toString();
		return str.isEmpty() ? null : str;
	}

	static String getStrOrDefault(Map<Object, Object> entries, String field,
			String defaultValue) {
		Object val = entries.get(field);
		if (val == null) {
			return defaultValue;
		}
		String str = val.toString();
		return str.isEmpty() ? defaultValue : str;
	}

	static long getLong(Map<Object, Object> entries, String field) {
		Object val = entries.get(field);
		if (val == null) {
			return 0;
		}
		return Long.parseLong(val.toString());
	}

	static String nullSafe(String value) {
		return value != null ? value : "";
	}
}
