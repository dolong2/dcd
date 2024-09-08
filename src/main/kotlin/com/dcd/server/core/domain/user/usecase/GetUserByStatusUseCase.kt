package com.dcd.server.core.domain.user.usecase

import com.dcd.server.core.common.annotation.ReadOnlyUseCase
import com.dcd.server.core.domain.user.dto.extension.toDto
import com.dcd.server.core.domain.user.dto.response.UserListResDto
import com.dcd.server.core.domain.user.model.enums.Status
import com.dcd.server.core.domain.user.spi.QueryUserPort

@ReadOnlyUseCase
class GetUserByStatusUseCase(
    private val queryUserPort: QueryUserPort
) {
    fun execute(status: Status): UserListResDto {
        val userResDtoList = queryUserPort.findByStatus(status)
            .map { it.toDto() }

        return UserListResDto(userResDtoList)
    }
}