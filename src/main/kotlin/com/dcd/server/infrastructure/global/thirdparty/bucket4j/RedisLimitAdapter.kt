package com.dcd.server.infrastructure.global.thirdparty.bucket4j

import com.dcd.server.core.common.annotation.Limit
import com.dcd.server.core.common.spi.LimitPort
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.BucketConfiguration
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce
import io.lettuce.core.RedisClient
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.time.Duration

@Primary
@Component
class RedisLimitAdapter(
    redisClient: RedisClient
) : LimitPort {
    private val logger = LoggerFactory.getLogger(RedisLimitAdapter::class.simpleName)
    private val bucketProxy = Bucket4jLettuce.casBasedBuilder(redisClient).build()

    override fun consumer(key: String, limit: Limit): Boolean =
        try {
            resolveToken(key, limit).tryConsume(1L)
        } catch (ex: Exception) {
            logger.error("Error consuming key $key")
            false
        }

    private fun resolveToken(key: String, limit: Limit): Bucket {
        val bandwidth = Bandwidth.builder()
            .capacity(limit.capacity)
            .refillGreedy(limit.refillAmount, Duration.ofMinutes(limit.refillDurationMinute))
            .initialTokens(limit.capacity)
            .build()
        val configSupplier = BucketConfiguration.builder().addLimit(bandwidth).build()

        return bucketProxy.builder().build(key.toByteArray(Charsets.UTF_8)) { configSupplier }
    }
}