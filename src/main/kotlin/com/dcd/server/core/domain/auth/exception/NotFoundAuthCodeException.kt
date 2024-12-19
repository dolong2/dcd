package com.dcd.server.core.domain.auth.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class NotFoundAuthCodeException : BasicException(ErrorCode.AUTH_CODE_NOT_FOUND) {
}