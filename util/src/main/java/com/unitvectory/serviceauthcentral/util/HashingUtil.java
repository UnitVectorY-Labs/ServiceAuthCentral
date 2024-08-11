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
package com.unitvectory.serviceauthcentral.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;

import com.unitvectory.serviceauthcentral.util.exception.InternalServerErrorException;

import lombok.NonNull;

/**
 * Utility class for hashing functions
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
public class HashingUtil {

	private static final HexFormat hexFormat = HexFormat.of();

	public static String sha256(@NonNull String textToHash) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] byteOfTextToHash = textToHash.getBytes(StandardCharsets.UTF_8);
			byte[] hashedByteArray = digest.digest(byteOfTextToHash);

			// Convert the byte array to a hex string using HexFormat
			return hexFormat.formatHex(hashedByteArray);
		} catch (NoSuchAlgorithmException e) {
			throw new InternalServerErrorException("Unable to find SHA-256 algorithm", e);
		}
	}

	public static String sha256Base64(@NonNull String textToHash) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] byteOfTextToHash = textToHash.getBytes(StandardCharsets.UTF_8);
			byte[] hashedByteArray = digest.digest(byteOfTextToHash);

			// Convert the byte array to a Base64 encoded string without padding
			return Base64.getUrlEncoder().withoutPadding().encodeToString(hashedByteArray);
		} catch (NoSuchAlgorithmException e) {
			throw new InternalServerErrorException("Unable to find SHA-256 algorithm", e);
		}
	}

}
