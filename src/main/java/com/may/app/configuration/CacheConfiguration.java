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
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableCaching
public class CacheConfiguration //implements RedisCacheManagerBuilderCustomizer {
{
	@Bean
	public RedisCacheManagerBuilderCustomizer myRedisCacheManagerBuilderCustomizer() {
		
	    return (builder) -> builder
	            .withCacheConfiguration("members",
	                    RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5))
	                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json())));
//	            .withCacheConfiguration("cache2",
//	                    RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(1)));
	    
	}
//	
//	@Bean
//	public RedisCacheConfiguration chacheManager(RedisConnectionFactory connectionFectory, ResourceLoader resourceLoader) {
//		RedisCacheConfiguration cacheconfiguration = RedisCacheConfiguration
//				.defaultCacheConfig()
//				.entryTtl(Duration.ofMinutes(5));
//		
//		cacheconfiguration.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new JdkSerializationRedisSerializer(resourceLoader.getClassLoader())));
//		
//		return cacheconfiguration;
//	}
}
