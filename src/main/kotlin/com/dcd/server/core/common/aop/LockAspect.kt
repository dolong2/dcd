package com.dcd.server.core.common.aop

import com.dcd.server.core.common.annotation.Lock
import com.dcd.server.core.common.aop.util.CustomExpressionParser
import com.dcd.server.core.common.spi.LockPort
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Aspect
@Component
class LockAspect(
    private val lockPort: LockPort,
) {
    @Around("@annotation(com.dcd.server.core.common.annotation.Lock)")
    @Throws(Throwable::class)
    fun redissonLock(joinPoint: ProceedingJoinPoint) {
        val signature = joinPoint.signature as MethodSignature
        val method: Method = signature.method
        val annotation: Lock = method.getAnnotation(Lock::class.java)
        val parameterValue =
            CustomExpressionParser.getDynamicValue(signature.parameterNames, joinPoint.args, annotation.lockName)
        val lockKey = "${method.declaringClass.simpleName}_${method.name}_$parameterValue"
        lockPort.lock(lockKey, annotation.waitTime, annotation.leaseTime) {
            joinPoint.proceed()
        }
    }
}