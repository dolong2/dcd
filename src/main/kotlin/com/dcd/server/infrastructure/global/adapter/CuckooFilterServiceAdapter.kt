package com.dcd.server.infrastructure.global.adapter

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.data.redis.core.StringRedisTemplate
import com.dcd.server.core.common.spi.CountBloomFilterPort
import com.dcd.server.core.common.service.exception.BloomFilterReservationException

@Service
class CuckooFilterServiceAdapter(
    private val redisTemplate: StringRedisTemplate
) : CountBloomFilterPort {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    override fun add(filterName: String, item: String): Boolean {
        // 해당하는 Cuckoo Filter가 존재하지 않으면 생성
        if (redisTemplate.hasKey(filterName).not()) {
            redisTemplate.execute { connection ->
                val result = connection.commands().execute("CF.RESERVE", filterName.toByteArray(), "100000".toByteArray()) as? String
                if (result != "OK" && result?.contains("item exists") != true) {
                    log.error("Failed to reserve Cuckoo Filter: {}", filterName)
                    throw BloomFilterReservationException()
                }
            }
        }

        return redisTemplate.execute { connection ->
            val result = connection.commands().execute("CF.ADDNX", filterName.toByteArray(), item.toByteArray())
            (result as? Long) == 1L
        } ?: false
    }

    override fun remove(filterName: String, item: String): Boolean {
        return redisTemplate.execute { connection ->
            val result = connection.commands().execute("CF.DEL", filterName.toByteArray(), item.toByteArray())
            (result as? Long) == 1L
        } ?: false
    }

    override fun exists(filterName: String, item: String): Boolean {
        return redisTemplate.execute { connection ->
            val result = connection.commands().execute("CF.EXISTS", filterName.toByteArray(), item.toByteArray())
            (result as? Long) == 1L
        } ?: false
    }
}
