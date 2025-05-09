package com.dcd.server.infrastructure.global.listener

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.redis.core.RedisKeyExpiredEvent
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class RedisEventListenerTest(
    private val redisTemplate: StringRedisTemplate,
    private val eventPublisher: ApplicationEventPublisher
) : BehaviorSpec({
    given("keyspace fullkey가 주어지고") {
        val givenKeyspace = "testKeyspace"
        val givenFullKey = "$givenKeyspace:testKey"
        val givenValue = "test"
        redisTemplate.opsForValue().set(givenFullKey, givenValue)
        redisTemplate.opsForSet().add(givenKeyspace, givenFullKey)

        `when`("RedisKeyExpiredEvent가 발행되면") {
            val givenEvent = RedisKeyExpiredEvent<Any>(givenFullKey.toByteArray())
            eventPublisher.publishEvent(givenEvent)

            then("연관된 SET이 삭제되어야함") {
                redisTemplate.opsForSet().isMember(givenKeyspace, givenFullKey) shouldBe false
            }
        }
    }
})