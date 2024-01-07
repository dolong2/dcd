package com.dcd.server.core.common.aop

import com.dcd.server.core.common.aop.exception.NotCertificateEmailException
import com.dcd.server.core.domain.auth.dto.request.NonAuthChangePasswordReqDto
import com.dcd.server.core.domain.auth.dto.request.SignUpReqDto
import com.dcd.server.core.domain.auth.exception.UserNotFoundException
import com.dcd.server.core.domain.auth.spi.CommandEmailAuthPort
import com.dcd.server.core.domain.auth.spi.QueryEmailAuthPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component

@Aspect
@Component
class EmailCertificateAspect(
    private val queryEmailAuthPort: QueryEmailAuthPort,
    private val commandEmailAuthPort: CommandEmailAuthPort,
    private val queryUserPort: QueryUserPort
) {
    @Pointcut("execution(* com.dcd.server.core.domain.auth.usecase.SignUpUseCase.execute(..)) " + "&& args(signUpReqDto)")
    fun signupUseCasePointcut(signUpReqDto: SignUpReqDto) {
    }

    @Pointcut("execution(* com.dcd.server.core.domain.auth.usecase.NonAuthChangePasswordUseCase.execute(..))" + "&& args(nonAuthChangePasswordReqDto)")
    fun changePasswordUseCasePointcut(nonAuthChangePasswordReqDto: NonAuthChangePasswordReqDto) {}

    @Before("signupUseCasePointcut(signUpReqDto)")
    private fun checkEmailCertificate(signUpReqDto: SignUpReqDto) {
        val emailAuthList = queryEmailAuthPort.findByEmail(signUpReqDto.email)
            .filter { it.certificate }
        if (emailAuthList.isEmpty())
            throw NotCertificateEmailException()
        commandEmailAuthPort.deleteByCode(emailAuthList[0].code)
    }

    @Before("changePasswordUseCasePointcut(nonAuthChangePasswordReqDto)")
    private fun checkEmailCertificate(nonAuthChangePasswordReqDto: NonAuthChangePasswordReqDto) {
        val email = nonAuthChangePasswordReqDto.email
        if (queryUserPort.existsByEmail(email).not())
            throw UserNotFoundException()
        val emailAuthList = queryEmailAuthPort.findByEmail(email)
            .filter { it.certificate }
        if (emailAuthList.isEmpty())
            throw NotCertificateEmailException()
        commandEmailAuthPort.deleteByCode(emailAuthList[0].code)
    }
}