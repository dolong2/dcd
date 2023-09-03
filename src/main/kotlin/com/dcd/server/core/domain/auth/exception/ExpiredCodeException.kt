package com.dcd.server.core.domain.auth.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class ExpiredCodeException : BasicException(ErrorCode.EXPIRED_AUTH_CODE) {
}