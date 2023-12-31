package com.unitvectory.auth.sign.gcp.util;

import java.nio.charset.StandardCharsets;

import com.google.common.hash.Hashing;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KidConverter {

	public static String hash(String keyName) {
		// This conversion must match between the key used to sign a JWT and the JWKS so
		// it can be validated properly. This means the keyName must include the full
		// key including the version
		return Hashing.sha256().hashString(keyName, StandardCharsets.UTF_8).toString();
	}
}
