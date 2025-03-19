package com.dcd.server.core.common.aop

import com.dcd.server.core.common.annotation.CheckEmailCertificate
import com.dcd.server.core.common.aop.exception.InvalidParsingObjectFieldException
import com.dcd.server.core.common.aop.exception.NotCertificateEmailException
import com.dcd.server.core.common.aop.util.CustomExpressionParser
import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode
import com.dcd.server.core.domain.auth.exception.UserNotFoundException
import com.dcd.server.core.domain.auth.spi.CommandEmailAuthPort
import com.dcd.server.core.domain.auth.spi.QueryEmailAuthPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import kotlin.reflect.full.memberProperties

@Aspect
@Component
class EmailCertificateAspect(
    private val queryEmailAuthPort: QueryEmailAuthPort,
    private val commandEmailAuthPort: CommandEmailAuthPort,
    private val queryUserPort: QueryUserPort
) {
    @Pointcut("@annotation(com.dcd.server.core.common.annotation.CheckEmailCertificate)")
    fun checkEmailCertificatePointcut() {}

    @Around("checkEmailCertificatePointcut()")
    fun checkEmailCertificate(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val annotation = method.getAnnotation(CheckEmailCertificate::class.java)

        val parameterObject =
            CustomExpressionParser.getDynamicValue(signature.parameterNames, joinPoint.args, annotation.target)
                ?: throw BasicException(ErrorCode.BAD_REQUEST)

        val email = getFieldValue(parameterObject, "email")
            ?: throw InvalidParsingObjectFieldException()

        if (queryUserPort.existsByEmail(email).not())
            throw UserNotFoundException()
        val emailAuthList = queryEmailAuthPort.findByEmail(email)
            .filter { it.certificate }
        if (emailAuthList.isEmpty())
            throw NotCertificateEmailException()

        val result = joinPoint.proceed()

        commandEmailAuthPort.deleteByCode(emailAuthList[0].code)

        return result
    }

    private fun getFieldValue(obj: Any, fieldName: String): String? {
        return obj::class.memberProperties
            .firstOrNull { it.name == fieldName }
            ?.getter
            ?.call(obj) as? String
    }
}