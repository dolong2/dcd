package com.dcd.server.core.common.aop

import com.dcd.server.core.common.annotation.Limit
import com.dcd.server.core.common.aop.exception.RequestLimitExceedException
import com.dcd.server.core.common.aop.util.CustomExpressionParser
import com.dcd.server.core.common.spi.LimitPort
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class LimitAspect(
    private val limitPort: LimitPort
) {
    @Around("@annotation(com.dcd.server.core.common.annotation.Limit)")
    fun checkLimit(joinPoint: ProceedingJoinPoint): Any {
        val methodSignature = joinPoint.signature as MethodSignature
        val limit = methodSignature.method.getAnnotation(Limit::class.java)

        // 메서드 호출에 대한 제한을 적용하기 위해 "{클래스 이름}.{메서드 이름}" 형태의 문자열 생성
        val methodName = "${methodSignature.declaringTypeName}.${methodSignature.name}"
        // limit 어노테이션의 target을 표현식 파싱
        val target = CustomExpressionParser.getDynamicValue(methodSignature.parameterNames, joinPoint.args, limit.target)

        val limitBucketKey = "$methodName-$target"
        // rateLimiter를 통해 토큰 사용 불가시 에러
        if (limitPort.consumer(limitBucketKey, limit).not())
            throw RequestLimitExceedException()

        return joinPoint.proceed()
    }
}