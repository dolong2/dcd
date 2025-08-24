package com.dcd.server.core.common.aop

import com.dcd.server.core.common.annotation.WorkspaceOwnerVerification
import com.dcd.server.core.common.aop.exception.InvalidParsingObjectFieldException
import com.dcd.server.core.common.aop.util.CustomExpressionParser
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class OwnerValidateAspect(
    private val getCurrentUserService: GetCurrentUserService,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val workspaceInfo: WorkspaceInfo
) {
    @Pointcut("@annotation(com.dcd.server.core.common.annotation.WorkspaceOwnerVerification)")
    fun workspaceOwnerVerificationPointcut() {}

    @Around("workspaceOwnerVerificationPointcut()")
    fun validWorkspaceOwner(joinPoint: ProceedingJoinPoint): Any? {
        val user = getCurrentUserService.getCurrentUser()

        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val annotation = method.getAnnotation(WorkspaceOwnerVerification::class.java)

        val workspaceId =
            CustomExpressionParser.getDynamicValue(signature.parameterNames, joinPoint.args, annotation.targetWorkspaceId)
                    as? String
                ?: throw InvalidParsingObjectFieldException()

        val workspace = (queryWorkspacePort.findById(workspaceId)
            ?: throw WorkspaceNotFoundException())

        val owner = workspace.owner
        if (owner != user)
            throw WorkspaceOwnerNotSameException()

        workspaceInfo.workspace = workspace

        return joinPoint.proceed()
    }
}