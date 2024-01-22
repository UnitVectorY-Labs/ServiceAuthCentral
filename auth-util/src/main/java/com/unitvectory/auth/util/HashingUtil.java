package com.unitvectory.auth.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;

import com.unitvectory.auth.util.exception.InternalServerErrorException;

import lombok.NonNull;

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
			return Base64.getEncoder().withoutPadding().encodeToString(hashedByteArray);
		} catch (NoSuchAlgorithmException e) {
			throw new InternalServerErrorException("Unable to find SHA-256 algorithm", e);
		}
	}

}
