package com.dcd.server.core.common.aop.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class InvalidParsingObjectFieldException : BasicException(ErrorCode.INVALID_PARSING_OBJECT_FIELD)