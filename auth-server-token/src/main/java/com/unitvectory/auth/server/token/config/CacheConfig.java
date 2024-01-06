package com.unitvectory.auth.server.token.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CacheConfig {

	@Bean
	public CacheManager cacheManager() {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager();

		// Define different cache configurations
		cacheManager.registerCustomCache("jwksCache",
				Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build());

		// ... Register more caches as needed

		return cacheManager;
	}
}
