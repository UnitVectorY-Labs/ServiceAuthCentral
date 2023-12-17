package com.unitvectory.serviceauthcentral.util;

import java.nio.charset.StandardCharsets;

import com.google.common.hash.Hashing;

public final class KidConverter {

	private KidConverter() {
	}

	public static String hash(String keyName) {
		// This conversion must match between the key used to sign a JWT and the JWKS so
		// it can be validated properly. This means the keyName must include the full
		// key including the version
		return Hashing.sha256().hashString(keyName, StandardCharsets.UTF_8).toString();
	}
}
