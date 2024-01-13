package com.unitvectory.auth.sign.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.unitvectory.auth.sign.model.RsaMoulousExponent;
import com.unitvectory.auth.util.exception.InternalServerErrorException;

public class RsaPemToModulusExponentMapperTest {

	@Test
	public void testConvertInvalid() {
		InternalServerErrorException thrown = assertThrows(InternalServerErrorException.class,
				() -> RsaPemToModulusExponentMapper.INSTANCE.convert("this is clearly not valid"),
				"Invalid PEM should throw an exception");

		assertEquals("Failed to convert RSA PEM key", thrown.getMessage());
	}

	@Test
	public void testConvertNull() {

		NullPointerException thrown = assertThrows(NullPointerException.class,
				() -> RsaPemToModulusExponentMapper.INSTANCE.convert(null),
				"Expected convert with null pemKey to throw exception");

		assertEquals("pemKey is marked non-null but is null", thrown.getMessage());
	}

	@Test
	public void testConvertInvalidKey() {

		// This key is intentionally malformed
		String pemKey = "-----BEGIN PUBLIC KEY-----\n"
				+ "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtqfCGqvSde8iPoarVSqm\n"
				+ "/dAhn97JJ1s8DxBlmnrG7hI99g2PMn+\n" + "zMq5o/QY+sVgMXVCrZeJrJa6vg/+K4kl89\n"
				+ "kthAUM40lqidum/Vrl5fw8UH7fv7+\n"
				+ "x2F6luAYpjlxy6NPJLHkV8PRpEZYdBDUUwakEyatbycBQo4fosLoQczWA10s+gsh\n"
				+ "PrQah14RzA3Oc0P+Rn244O+LwdV//WR1\n" + "-----END PUBLIC KEY-----\n" + "";

		InternalServerErrorException thrown = assertThrows(InternalServerErrorException.class,
				() -> RsaPemToModulusExponentMapper.INSTANCE.convert(pemKey),
				"Invalid PEM should throw an exception");

		assertEquals("Failed to convert RSA PEM key", thrown.getMessage());

	}

	@Test
	public void testConvertSuccess() {

		// TODO: Load this from public_key.pem resource
		String pemKey = "-----BEGIN PUBLIC KEY-----\n"
				+ "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtqfCGqvSde8iPoarVSqm\n"
				+ "/dAhn97JJ1s8DxBlmnrG7hI99g2PMn+KkuhbiYM07dU2RGsESsNPVSe7cWOpBz9A\n"
				+ "zMq5o/QY+sVgMXVCrZeJrJa6vg/cZ7N674JSXbLIVQCoWc9GPPk9NaJX5+K4kl89\n"
				+ "kthAUM40lqidum/Vrl5fw8UH7fv7+kkCdDcT94jpIrirRKNnc3KMpwAXIPCBUZTb\n"
				+ "x2F6luAYpjlxy6NPJLHkV8PRpEZYdBDUUwakEyatbycBQo4fosLoQczWA10s+gsh\n"
				+ "PrQah14RzA3Oc0P+Rn244O+LwdV/7wwd8eBlL6XSCYjp7WcBsZRgW3yAFH9m/WR1\n"
				+ "jwIDAQAB\n" + "-----END PUBLIC KEY-----\n" + "";

		RsaMoulousExponent rsaMoulousExponent =
				RsaPemToModulusExponentMapper.INSTANCE.convert(pemKey);

		assertNotNull(rsaMoulousExponent);

		assertEquals(
				"tqfCGqvSde8iPoarVSqm_dAhn97JJ1s8DxBlmnrG7hI99g2PMn-KkuhbiYM07dU2RGsESsNPVSe7cWOpBz9AzMq5o_QY-sVgMXVCrZeJrJa6vg_cZ7N674JSXbLIVQCoWc9GPPk9NaJX5-K4kl89kthAUM40lqidum_Vrl5fw8UH7fv7-kkCdDcT94jpIrirRKNnc3KMpwAXIPCBUZTbx2F6luAYpjlxy6NPJLHkV8PRpEZYdBDUUwakEyatbycBQo4fosLoQczWA10s-gshPrQah14RzA3Oc0P-Rn244O-LwdV_7wwd8eBlL6XSCYjp7WcBsZRgW3yAFH9m_WR1jw",
				rsaMoulousExponent.getModulus());
		assertEquals("AQAB", rsaMoulousExponent.getExponent());

	}
}
