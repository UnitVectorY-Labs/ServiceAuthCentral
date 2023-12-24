package com.unitvectory.serviceauthcentral.datamodel.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class HashingUtil {

	public static String sha256(String textToHash) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] byteOfTextToHash = textToHash.getBytes(StandardCharsets.UTF_8);
			byte[] hashedByetArray = digest.digest(byteOfTextToHash);
			String encoded = Base64.getEncoder().encodeToString(hashedByetArray);
			return encoded;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
