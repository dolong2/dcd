package com.dcd.server.core.common.aop

import com.dcd.server.core.common.annotation.CheckEmailCertificate
import com.dcd.server.core.common.aop.exception.InvalidParsingObjectFieldException
import com.dcd.server.core.common.aop.exception.NotCertificateEmailException
import com.dcd.server.core.common.aop.util.CustomExpressionParser
import com.dcd.server.core.domain.auth.spi.CommandEmailAuthBlackListPort
import com.dcd.server.core.domain.auth.spi.CommandEmailAuthPort
import com.dcd.server.core.domain.auth.spi.QueryEmailAuthBlackListPort
import com.dcd.server.core.domain.auth.spi.QueryEmailAuthPort
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class EmailCertificateAspect(
    private val queryEmailAuthPort: QueryEmailAuthPort,
    private val commandEmailAuthPort: CommandEmailAuthPort,
    private val queryEmailAuthBlackListPort: QueryEmailAuthBlackListPort,
    private val commandEmailAuthBlackListPort: CommandEmailAuthBlackListPort
) {
    @Pointcut("@annotation(com.dcd.server.core.common.annotation.CheckEmailCertificate)")
    fun checkEmailCertificatePointcut() {}

    @Around("checkEmailCertificatePointcut()")
    fun checkEmailCertificate(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val annotation = method.getAnnotation(CheckEmailCertificate::class.java)

        val email =
            CustomExpressionParser.getDynamicValue(signature.parameterNames, joinPoint.args, annotation.target)
                as? String
                ?: throw InvalidParsingObjectFieldException()

        if (queryEmailAuthBlackListPort.isBlocked(email, annotation.usage))
            throw NotCertificateEmailException()

        val emailAuthList = queryEmailAuthPort.findByEmail(email)
            .filter { it.certificate && it.usage == annotation.usage }

        if (emailAuthList.isEmpty()) {
            val failedAuth = queryEmailAuthPort.findByEmailAndUsage(email, annotation.usage)
            if (failedAuth != null) {
                val threshold = annotation.usage.failThreshold
                val newFailCount = failedAuth.failCount + 1

                if (newFailCount >= threshold) {
                    // 블랙리스트에 추가
                    commandEmailAuthBlackListPort.addToBlackList(email, annotation.usage)
                    throw NotCertificateEmailException()
                }

                // failCount 증가
                commandEmailAuthPort.incrementFailCount(email, annotation.usage)
            }
            throw NotCertificateEmailException()
        }

        val result = joinPoint.proceed()

        commandEmailAuthPort.deleteByCode(emailAuthList[0].code)
        commandEmailAuthPort.resetFailCount(email)

        return result
    }
}