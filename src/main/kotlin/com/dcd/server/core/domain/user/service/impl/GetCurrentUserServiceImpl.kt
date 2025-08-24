package com.dcd.server.core.domain.user.service.impl

import com.dcd.server.core.common.service.SecurityService
import com.dcd.server.core.domain.auth.exception.UserNotFoundException
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.user.spi.QueryUserPort
import org.springframework.stereotype.Service

@Service
class GetCurrentUserServiceImpl(
    private val securityService: SecurityService,
    private val queryUserPort: QueryUserPort
) : GetCurrentUserService {
    override fun getCurrentUser(): User {
        val userId = securityService.getCurrentUserId()
        return (queryUserPort.findById(userId)
            ?: throw UserNotFoundException())
    }
}