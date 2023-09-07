package com.dcd.server.core.domain.auth.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class ExpiredRefreshTokenException : BasicException(ErrorCode.EXPIRED_REFRESH_TOKEN) {
}