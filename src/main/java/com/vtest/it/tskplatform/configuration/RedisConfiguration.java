package com.vtest.it.tskplatform.configuration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Configuration
public class RedisConfiguration extends CachingConfigurerSupport {
    @Bean("redisCacheManager")
    @Primary
    public CacheManager cacheManager(JedisConnectionFactory jedisConnectionFactory){
        RedisCacheConfiguration configuration=RedisCacheConfiguration.defaultCacheConfig().disableCachingNullValues();
        Set<String> cacheNames=new HashSet<>();
        cacheNames.add("SystemPropertiesCache");
        cacheNames.add("MesInformationCache");
        Map<String,RedisCacheConfiguration> cacheConfigurationMap=new HashMap<>();
        cacheConfigurationMap.put("SystemPropertiesCache",configuration);
        cacheConfigurationMap.put("MesInformationCache",configuration.entryTtl(Duration.ofDays(3)).serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())));
        return RedisCacheManager.builder(jedisConnectionFactory).initialCacheNames(cacheNames).withInitialCacheConfigurations(cacheConfigurationMap).build();
    }
}
