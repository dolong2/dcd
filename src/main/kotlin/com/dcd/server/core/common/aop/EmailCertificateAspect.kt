package com.dcd.server.core.common.aop

import com.dcd.server.core.common.aop.exception.NotCertificateEmailException
import com.dcd.server.core.domain.auth.dto.request.SignUpRequestDto
import com.dcd.server.core.domain.auth.spi.CommandEmailAuthPort
import com.dcd.server.core.domain.auth.spi.QueryEmailAuthPort
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component

@Aspect
@Component
class EmailCertificateAspect(
    private val queryEmailAuthPort: QueryEmailAuthPort,
    private val commandEmailAuthPort: CommandEmailAuthPort
) {
    @Pointcut("execution(* com.dcd.server.core.domain.auth.usecase.SignUpUseCase.execute(..)) " +  "&& args(signUpRequestDto)")
    fun signupUseCasePointcut(signUpRequestDto: SignUpRequestDto) {
    }

    @Before("signupUseCasePointcut(signUpRequestDto)")
    private fun checkEmailCertificate(signUpRequestDto: SignUpRequestDto) {
        val emailAuthList = queryEmailAuthPort.findByEmail(signUpRequestDto.email)
            .filter { it.certificate }
        if (emailAuthList.isEmpty())
            throw NotCertificateEmailException()
        commandEmailAuthPort.deleteByCode(emailAuthList[0].code)
    }
}