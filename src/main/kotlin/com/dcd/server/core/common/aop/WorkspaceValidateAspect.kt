package com.dcd.server.core.common.aop

import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component

@Aspect
@Component
class WorkspaceValidateAspect(
    private val getCurrentUserService: GetCurrentUserService,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val queryApplicationPort: QueryApplicationPort
) {
    @Pointcut("@annotation(com.dcd.server.core.common.annotation.WorkspaceOwnerVerification)")
    fun verificationPointcut() {}

    @Before("verificationPointcut() && args(id, ..)")
    fun validWorkspaceOwner(id: String) {
        val user = getCurrentUserService.getCurrentUser()

        val workspace = (findWorkspace(id)
            ?: throw WorkspaceNotFoundException())

        if (!workspace.owner.equals(user))
            throw WorkspaceOwnerNotSameException()
    }

    private fun findWorkspace(id: String): Workspace? =
        queryWorkspacePort.findById(id)
            ?: queryApplicationPort.findById(id)?.workspace
}