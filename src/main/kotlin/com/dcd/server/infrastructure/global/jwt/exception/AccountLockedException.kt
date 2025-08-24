package com.dcd.server.infrastructure.global.jwt.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class AccountLockedException : BasicException(ErrorCode.ACCOUNT_LOCKED) {
}