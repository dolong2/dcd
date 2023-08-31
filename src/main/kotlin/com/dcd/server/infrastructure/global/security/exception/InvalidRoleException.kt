package com.dcd.server.infrastructure.global.security.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class InvalidRoleException : BasicException(ErrorCode.INVALID_ROLE) {
}