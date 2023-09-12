package com.dcd.server.core.domain.user.service

import com.dcd.server.core.domain.user.model.User

interface GetCurrentUserService {
    fun getCurrentUser(): User
}