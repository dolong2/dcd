package com.dcd.server.infrastructure.global.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisKeyValueAdapter
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration


@Configuration
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
class RedisConfig {
    @Value("\${spring.data.redis.host}")
    private val host: String = ""

    @Value("\${spring.data.redis.port}")
    private val port = 0
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory(host, port)
    }

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        config.useSingleServer().setAddress("redis://$host:$port")
        return Redisson.create(config)
    }

    @Bean
    fun cacheManager(redisConnectionFactory: RedisConnectionFactory): RedisCacheManager {
        val objectMapper = ObjectMapper().registerKotlinModule()
        // 안전한 default typing 활성화 (버전 및 보안 이슈에 따라 설정값 조정)
        val ptv = BasicPolymorphicTypeValidator.builder()
            .allowIfSubType(Any::class.java)
            .build()
        objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.EVERYTHING)

        val jsonRedisSerializer = GenericJackson2JsonRedisSerializer(objectMapper)

        val config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(5)) // 캐시 TTL 5시간 설정
            .disableCachingNullValues()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonRedisSerializer))

        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(config)
            .build()
    }
}