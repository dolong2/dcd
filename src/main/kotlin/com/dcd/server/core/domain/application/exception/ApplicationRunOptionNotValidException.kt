package com.dcd.server.core.domain.application.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class ApplicationRunOptionNotValidException : BasicException(ErrorCode.APPLICATION_OPTION_NOT_VALID) {
}