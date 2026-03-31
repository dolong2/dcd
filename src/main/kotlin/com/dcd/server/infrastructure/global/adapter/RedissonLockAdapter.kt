package com.dcd.server.infrastructure.global.adapter

import com.dcd.server.core.common.spi.LockPort
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

@Service
class RedissonLockAdapter(
    private val redissonClient: RedissonClient
) : LockPort {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    override fun <T> lock(lockKey: String, waitTime: Long, leaseTime: Long, block: () -> T): T? {
        val lock = redissonClient.getLock(lockKey)
        try {
            val lockable = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS)
            if (!lockable) {
                log.error("Lock 획득 실패: $lockKey")
                return null
            }
            log.debug("로직 수행")
            return block()
        } catch (e: InterruptedException) {
            log.error("에러 발생")
            throw e
        } finally {
            if (lock.isHeldByCurrentThread) {
                log.debug("Lock 해제: $lockKey")
                lock.unlock()
            }
        }
    }
}
