package com.dcd.server.core.common.service.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class PasswordNotCorrectException : BasicException(ErrorCode.PASSWORD_NOT_CORRECT) {
}