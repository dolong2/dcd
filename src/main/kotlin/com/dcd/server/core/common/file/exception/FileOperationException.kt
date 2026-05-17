package com.dcd.server.core.common.file.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class FileOperationException : BasicException(ErrorCode.FILE_OPERATION_FAILED)
