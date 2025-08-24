package com.dcd.server.presentation.common.error.response

import com.dcd.server.core.common.error.ErrorCode

data class ErrorResponse(
    val status: Int,
    val message: String
) {
    constructor(errorCode: ErrorCode) : this(errorCode.code, errorCode.msg)
}