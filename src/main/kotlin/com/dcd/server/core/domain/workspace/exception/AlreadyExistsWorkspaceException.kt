package com.dcd.server.core.domain.workspace.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class AlreadyExistsWorkspaceException : BasicException(ErrorCode.ALREADY_EXISTS_WORKSPACE) {

}
