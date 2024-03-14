package com.dcd.server.core.common.aop

import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component

@Aspect
@Component
class WorkspaceValidateAspect {
    @Pointcut("@annotation(com.dcd.server.core.annotation.WorkspaceOwnerVerification)")
    fun verificationPointcut() {}

}