package com.unitvectory.auth.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class HashingUtilTest {

	@Test
	public void testHashWithValidInput() {
		String testString = "Hello, World!";
		// Known SHA-256 hash of "Hello, World!
		String expectedHash = "dffd6021bb2bd5b0af676290809ec3a53191dd81c7f70a4b28688a362182986f";
		String actualHash = HashingUtil.sha256(testString);
		assertEquals(expectedHash, actualHash,
				"The SHA-256 hash should match the known hash value.");
	}

	@Test
	public void testHashWithEmptyString() {
		String testString = "";
		// Known SHA-256 hash of an empty string
		String expectedHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
		String actualHash = HashingUtil.sha256(testString);
		assertEquals(expectedHash, actualHash,
				"The SHA-256 hash of an empty string should match the known hash value.");
	}

	@Test
	public void testHashWithNullInput() {
		assertThrows(NullPointerException.class, () -> {
			HashingUtil.sha256(null);
		}, "Passing null to sha256 should throw a NullPointerException.");
	}

	@Test
	public void testHashWithDifferentInputs() {
		String input1 = "text1";
		String input2 = "text2";
		assertNotEquals(HashingUtil.sha256(input1), HashingUtil.sha256(input2),
				"Different inputs should produce different hashes.");
	}

	@Test
	public void testAlgorithmConsistency() {
		String testString = "consistent";
		String firstHash = HashingUtil.sha256(testString);
		String secondHash = HashingUtil.sha256(testString);
		assertEquals(firstHash, secondHash,
				"Hashing the same string multiple times should produce the same result.");
	}
}
