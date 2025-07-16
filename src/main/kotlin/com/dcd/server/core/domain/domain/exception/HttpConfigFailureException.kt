package com.dcd.server.core.domain.domain.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class HttpConfigFailureException : BasicException(ErrorCode.FAILURE_HTTP_CONFIG) {

}
