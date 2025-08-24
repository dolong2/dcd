package com.dcd.server.core.common.error

open class BasicException(
    val errorCode: ErrorCode
) : RuntimeException()