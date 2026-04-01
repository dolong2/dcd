package com.dcd.server.infrastructure.global.adapter

import org.slf4j.LoggerFactory
import org.redisson.api.RedissonClient
import com.dcd.server.core.common.spi.CountBloomFilterPort
import com.dcd.server.core.common.service.exception.BloomFilterReservationException
import org.redisson.client.RedisException
import org.springframework.stereotype.Component

@Component
class CuckooFilterAdapter(
    private val redissonClient: RedissonClient
) : CountBloomFilterPort {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    companion object {
        private const val DEFAULT_CAPACITY = 100000L
    }

    override fun add(filterName: String, item: String): Boolean {
        return try {
            val cuckooFilter = redissonClient.getCuckooFilter<String>(filterName)

            // CuckooFilter가 없으면 생성
            if (!cuckooFilter.isExists) {
                try {
                    cuckooFilter.init(DEFAULT_CAPACITY)
                } catch (e: Exception) {
                    log.error("Error initializing Cuckoo Filter: {}", filterName, e)
                    throw BloomFilterReservationException()
                }
            }

            cuckooFilter.addIfAbsent(item)
        } catch (e: Exception) {
            if (e is BloomFilterReservationException) throw e
            log.error("Error adding to Cuckoo Filter: {}", filterName, e)
            true
        }
    }

    override fun remove(filterName: String, item: String): Boolean {
        return try {
            val cuckooFilter = redissonClient.getCuckooFilter<String>(filterName)
            cuckooFilter.remove(item)
        } catch (e: Exception) {
            // 에러 발생시 로깅후 false positive
            log.error("Error removing from Cuckoo Filter: {}", filterName, e)
            true
        }
    }

    override fun exists(filterName: String, item: String): Boolean {
        return try {
            val cuckooFilter = redissonClient.getCuckooFilter<String>(filterName)
            cuckooFilter.exists(item)
        } catch (e: Exception) {
            log.error("Error checking existence in Cuckoo Filter: {}", filterName, e)
            true
        }
    }
}
