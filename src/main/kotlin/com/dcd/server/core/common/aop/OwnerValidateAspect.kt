package com.dcd.server.core.common.aop

import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component

@Aspect
@Component
class OwnerValidateAspect(
    private val getCurrentUserService: GetCurrentUserService,
    private val queryWorkspacePort: QueryWorkspacePort
) {
    @Pointcut("@annotation(com.dcd.server.core.common.annotation.WorkspaceOwnerVerification)")
    fun workspaceOwnerVerificationPointcut() {}

    @Before("workspaceOwnerVerificationPointcut() && args(id, ..)")
    fun validWorkspaceOwner(id: String) {
        val user = getCurrentUserService.getCurrentUser()

        val workspace = (queryWorkspacePort.findById(id)
            ?: throw WorkspaceNotFoundException())

        val owner = workspace.owner
        if (owner.id != user.id)
            throw WorkspaceOwnerNotSameException()
    }
}