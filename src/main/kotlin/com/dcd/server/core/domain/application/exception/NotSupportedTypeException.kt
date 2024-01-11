package com.dcd.server.core.domain.application.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class NotSupportedTypeException : BasicException(ErrorCode.NOT_SUPPORTED_APPLICATION_TYPE) {
}