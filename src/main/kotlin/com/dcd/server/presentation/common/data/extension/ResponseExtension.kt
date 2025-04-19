package com.dcd.server.presentation.common.data.extension

import com.dcd.server.core.common.data.dto.response.ListResDto
import com.dcd.server.presentation.common.data.response.ListResponse

fun <T, R> ListResDto<T>.toResponse(transform: (T) -> R): ListResponse<R> {
    return ListResponse(
        list = this.list.map(transform)
    )
}