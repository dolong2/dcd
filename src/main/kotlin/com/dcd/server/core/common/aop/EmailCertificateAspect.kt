package com.dcd.server.core.common.aop

import com.dcd.server.core.common.annotation.CheckEmailCertificate
import com.dcd.server.core.common.aop.exception.InvalidParsingObjectFieldException
import com.dcd.server.core.common.aop.exception.NotCertificateEmailException
import com.dcd.server.core.common.aop.util.CustomExpressionParser
import com.dcd.server.core.domain.auth.spi.CommandEmailAuthPort
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
    private val commandEmailAuthPort: CommandEmailAuthPort
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

        val emailAuthList = queryEmailAuthPort.findByEmail(email)
            .filter { it.certificate && it.usage == annotation.usage}
        if (emailAuthList.isEmpty())
            throw NotCertificateEmailException()

        val result = joinPoint.proceed()

        commandEmailAuthPort.deleteByCode(emailAuthList[0].code)

        return result
    }
}