package com.dcd.server.core.common.aop

import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component

@Aspect
@Component
class WorkspaceValidateAspect(
    private val getCurrentUserService: GetCurrentUserService,
    private val queryApplicationPort: QueryApplicationPort
) {
    @Pointcut("@annotation(com.dcd.server.core.annotation.WorkspaceOwnerVerification)")
    fun verificationPointcut() {}

    @Before("verificationPointcut() && args(id, ..)")
    fun validWorkspaceOwner(id: String) {
        val user = getCurrentUserService.getCurrentUser()

        val application = queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException()

        if (application.workspace.owner.equals(user))
            throw WorkspaceOwnerNotSameException()
    }
}