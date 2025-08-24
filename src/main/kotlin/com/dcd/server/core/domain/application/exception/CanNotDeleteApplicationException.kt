package com.dcd.server.core.domain.application.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class CanNotDeleteApplicationException : BasicException(ErrorCode.CAN_NOT_DELETE_APPLICATION) {
}