package com.dcd.server.core.domain.workspace.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class WorkspaceConnectionException : BasicException(ErrorCode.WORKSPACE_CONNECTION_FAILED) {
}