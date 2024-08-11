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
package com.unitvectory.auth.sign.model;

import lombok.Builder;
import lombok.Value;

/**
 * Represents the modulus and exponent of an RSA public key.
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Value
@Builder
public class RsaMoulousExponent {

	/**
	 * The modulus of the RSA key. It's a base64 URL encoded string. The modulus is a part of the
	 * public key used in cryptographic operations.
	 *
	 * @return the modulus of the RSA key.
	 */
	private final String modulus;

	/**
	 * The exponent of the RSA key. It's a base64 URL encoded string. The exponent is a part of the
	 * public key used in cryptographic operations.
	 *
	 * @return the exponent of the RSA key.
	 */
	private final String exponent;
}
