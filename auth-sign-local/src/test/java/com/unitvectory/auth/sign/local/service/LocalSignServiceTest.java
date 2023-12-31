package com.unitvectory.auth.sign.local.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@ContextConfiguration(classes = LocalSignServiceTest.TestConfig.class)
public class LocalSignServiceTest {

	@Autowired
	private LocalSignService localSignService;

	@TestConfiguration
	public static class TestConfig {
		@Bean
		public LocalSignService localSignService() {
			return new LocalSignService();
		}
	}

	@Test
	public void testActiveKid() {

		String activeKid = this.localSignService.getActiveKid(0);

		System.out.println(activeKid);

		assertEquals("foo", activeKid);

	}
}
