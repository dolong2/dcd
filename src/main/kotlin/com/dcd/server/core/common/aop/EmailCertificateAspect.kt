package com.dcd.server.core.common.aop

import com.dcd.server.core.common.aop.exception.NotCertificateEmailException
import com.dcd.server.core.domain.auth.dto.request.PasswordChangeReqDto
import com.dcd.server.core.domain.auth.dto.request.SignUpReqDto
import com.dcd.server.core.domain.auth.spi.CommandEmailAuthPort
import com.dcd.server.core.domain.auth.spi.QueryEmailAuthPort
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component

@Aspect
@Component
class EmailCertificateAspect(
    private val queryEmailAuthPort: QueryEmailAuthPort,
    private val commandEmailAuthPort: CommandEmailAuthPort,
    private val getCurrentUserService: GetCurrentUserService
) {
    @Pointcut("execution(* com.dcd.server.core.domain.auth.usecase.SignUpUseCase.execute(..)) " + "&& args(signUpReqDto)")
    fun signupUseCasePointcut(signUpReqDto: SignUpReqDto) {
    }

    @Pointcut("execution(* com.dcd.server.core.domain.auth.usecase.ChangePasswordUseCase.execute(..))" + "&& args(passwordChangeReqDto)")
    fun changePasswordUseCasePointcut(passwordChangeReqDto: PasswordChangeReqDto) {}

    @Before("signupUseCasePointcut(signUpReqDto)")
    private fun checkEmailCertificate(signUpReqDto: SignUpReqDto) {
        val emailAuthList = queryEmailAuthPort.findByEmail(signUpReqDto.email)
            .filter { it.certificate }
        if (emailAuthList.isEmpty())
            throw NotCertificateEmailException()
        commandEmailAuthPort.deleteByCode(emailAuthList[0].code)
    }

    @Before("changePasswordUseCasePointcut(passwordChangeReqDto)")
    private fun checkEmailCertificate() {
        val user = getCurrentUserService.getCurrentUser()
        val emailAuthList = queryEmailAuthPort.findByEmail(user.email)
            .filter { it.certificate }
        if (emailAuthList.isEmpty())
            throw NotCertificateEmailException()
        commandEmailAuthPort.deleteByCode(emailAuthList[0].code)
    }
}