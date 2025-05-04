package com.dcd.server.core.common.aop.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class RequestLimitExceedException : BasicException(ErrorCode.REQUEST_LIMIT_EXCEEDED) {

}
