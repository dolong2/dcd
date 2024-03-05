package com.dcd.server.core.domain.application.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class CanNotDeployApplicationException : BasicException(ErrorCode.CAN_NOT_DEPLOY_APPLICATION) {
}