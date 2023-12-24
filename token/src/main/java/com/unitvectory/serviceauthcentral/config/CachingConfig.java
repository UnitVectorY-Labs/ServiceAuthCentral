package com.unitvectory.serviceauthcentral.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CachingConfig {

	@Autowired
	private AppConfig appConfig;

	@Bean
	public CaffeineCacheManager cacheManager() {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager("jwksCache", "publicKeyCache", "activeKeyCache",
				"keySetLookupCache");

		// The JWKS response only changes when a signing key is added or removed. These
		// operations are done deliberately as caching at the clients is also included
		// therefore a liberal cache here is acceptable
		cacheManager.registerCustomCache("jwksCache",
				Caffeine.newBuilder().expireAfterWrite(this.appConfig.getCacheJwksHours(), TimeUnit.HOURS).build());

		// The public keys can never change, therefore they can be cached indefinitely
		// once they are retrieved so avoid these API calls
		cacheManager.registerCustomCache("publicKeyCache", Caffeine.newBuilder().build());

		// The active key is computed based on the list of all keys, no need to
		// recompute this every call, add some efficiency here
		cacheManager.registerCustomCache("activeKeyCache",
				Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build());

		// The cache for looking up external JWKS keys
		cacheManager.registerCustomCache("keySetLookupCache",
				Caffeine.newBuilder().expireAfterWrite(this.appConfig.getCacheJwksHours(), TimeUnit.HOURS).build());

		return cacheManager;
	}
}