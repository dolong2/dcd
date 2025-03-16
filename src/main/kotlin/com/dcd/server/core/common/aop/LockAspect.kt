package com.dcd.server.core.common.aop

import com.dcd.server.core.common.annotation.Lock
import com.dcd.server.core.common.aop.util.CustomExpressionParser
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.util.concurrent.TimeUnit

@Aspect
@Component
class LockAspect(
    private val redissonClient: RedissonClient,
) {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    @Around("@annotation(com.dcd.server.core.common.annotation.Lock)")
    @Throws(Throwable::class)
    fun redissonLock(joinPoint: ProceedingJoinPoint) {
        val signature = joinPoint.signature as MethodSignature
        val method: Method = signature.method
        val annotation: Lock = method.getAnnotation(Lock::class.java)
        val parameterValue =
            CustomExpressionParser.getDynamicValue(signature.parameterNames, joinPoint.args, annotation.lockName)
        val lockKey = "${method.declaringClass.simpleName}_${method.name}_$parameterValue"
        val lock = redissonClient.getLock(lockKey)
        try {
            val lockable = lock.tryLock(annotation.waitTime, annotation.leaseTime, TimeUnit.MILLISECONDS)
            if (!lockable) {
                log.debug("Lock 획득 실패={}", lockKey)
                return
            }
            log.debug("로직 수행")
            joinPoint.proceed()
        } catch (e: InterruptedException) {
            log.error("에러 발생")
            throw e
        } finally {
            log.debug("락 해제")
            lock.unlock()
        }
    }
}