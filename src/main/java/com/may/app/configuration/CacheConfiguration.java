package com.may.app.configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

@Configuration
@EnableCaching
public class CacheConfiguration //implements RedisCacheManagerBuilderCustomizer {
{
	@Bean
	public RedisCacheManagerBuilderCustomizer myRedisCacheManagerBuilderCustomizer() {
	    return (builder) -> builder
	            .withCacheConfiguration("members",
	                    RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
	            .withCacheConfiguration("cache2",
	                    RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(1)));

	}
}
