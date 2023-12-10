package com.unitvectory.serviceauthcentral.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CachingConfig {

	@Value("${serviceauthcentral.cache.jwks.hours}")
	private int cacheJwksHours;

	@Bean
	public int cacheJwksHours() {
		return this.cacheJwksHours;
	}

	@Bean
	public CaffeineCacheManager cacheManager() {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager("jwksCache");

		// The JWKS response only changes when a signing key is added or removed. These
		// operations are done deliberately as caching at the clients is also included
		// therefore a liberal cache here is acceptable
		cacheManager.registerCustomCache("jwksCache",
				Caffeine.newBuilder().expireAfterWrite(this.cacheJwksHours, TimeUnit.HOURS).build());
		return cacheManager;
	}
}