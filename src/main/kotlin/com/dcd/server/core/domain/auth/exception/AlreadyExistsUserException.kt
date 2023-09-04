package com.dcd.server.core.domain.auth.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class AlreadyExistsUserException : BasicException(ErrorCode.ALREADY_USER_EXIST) {
}