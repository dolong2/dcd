package com.dcd.server.infrastructure.global.thirdparty.bucket4j

import com.dcd.server.core.common.annotation.Limit
import com.dcd.server.core.common.spi.LimitPort
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Component
class LocalLimitAdapter : LimitPort {
    private val bucketMap: MutableMap<String, Bucket> = ConcurrentHashMap()

    override fun consumer(key: String, limit: Limit): Boolean =
        resolveToken(key, limit).tryConsume(1L)

    private fun resolveToken(key: String, limit: Limit): Bucket =
        bucketMap.computeIfAbsent(key) {
            val bandwidth = Bandwidth.builder()
                .capacity(limit.capacity)
                .refillGreedy(limit.refillAmount, Duration.ofMinutes(limit.refillDurationMinute))
                .initialTokens(limit.capacity)
                .build()

            Bucket.builder().addLimit(bandwidth).build()
        }
}