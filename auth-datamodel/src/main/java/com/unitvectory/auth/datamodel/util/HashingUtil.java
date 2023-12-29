package com.unitvectory.auth.datamodel.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class HashingUtil {

	private static final HexFormat hexFormat = HexFormat.of();

	public static String sha256(String textToHash) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] byteOfTextToHash = textToHash.getBytes(StandardCharsets.UTF_8);
			byte[] hashedByteArray = digest.digest(byteOfTextToHash);

			// Convert the byte array to a hex string using HexFormat
			return hexFormat.formatHex(hashedByteArray);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Unable to find SHA-256 algorithm", e);
		}
	}

}
