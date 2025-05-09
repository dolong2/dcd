package com.dcd.server.infrastructure.global.listener

import org.springframework.context.event.EventListener
import org.springframework.data.redis.core.RedisKeyExpiredEvent
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class RedisEventListener(
    private val redisTemplate: StringRedisTemplate
) {
    @EventListener
    @Transactional(rollbackFor = [Exception::class])
    fun process(event: RedisKeyExpiredEvent<Any>) {
        val expiredKey = event.source.toString()

        val keyspacePrefix = expiredKey.substringBeforeLast(":")

        // 관련 SET(키스페이스) 삭제
        redisTemplate.delete(keyspacePrefix)
    }
}