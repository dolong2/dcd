package com.dcd.server.infrastructure.global.adapter

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.redisson.api.RedissonClient
import org.redisson.client.RedisException
import org.redisson.client.codec.StringCodec
import com.dcd.server.core.common.spi.CountBloomFilterPort
import com.dcd.server.core.common.service.exception.BloomFilterReservationException

@Service
class CuckooFilterServiceAdapter(
    private val redissonClient: RedissonClient
) : CountBloomFilterPort {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    private val DEFAULT_CAPACITY = 10000L

    override fun add(filterName: String, item: String): Boolean {
        return try {
            val cuckooFilter = redissonClient.getCuckooFilter<String>(filterName)
            
            // CuckooFilter가 없으면 생성
            if (!cuckooFilter.exists()) {
                cuckooFilter.init(DEFAULT_CAPACITY)
            }
            
            cuckooFilter.add(item)
        } catch (e: Exception) {
            if (e is BloomFilterReservationException) throw e
            log.error("Error adding to Cuckoo Filter: {}", filterName, e)
            throw BloomFilterReservationException()
        }
    }

    override fun remove(filterName: String, item: String): Boolean {
        return try {
            val cuckooFilter = redissonClient.getCuckooFilter<String>(filterName)
            cuckooFilter.remove(item)
        } catch (e: Exception) {
            log.error("Error removing from Cuckoo Filter: {}", filterName, e)
            throw BloomFilterReservationException()
        }
    }

    override fun exists(filterName: String, item: String): Boolean {
        return try {
            val cuckooFilter = redissonClient.getCuckooFilter<String>(filterName)
            cuckooFilter.exists(item)
        } catch (e: Exception) {
            log.error("Error checking existence in Cuckoo Filter: {}", filterName, e)
            throw BloomFilterReservationException()
        }
    }
}
