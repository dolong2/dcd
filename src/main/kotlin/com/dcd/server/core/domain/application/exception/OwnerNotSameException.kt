package com.dcd.server.core.domain.application.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class OwnerNotSameException : BasicException(ErrorCode.NOT_SAME_APPLICATION_OWNER) {
}