package com.dcd.server.core.domain.workspace.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class WorkspaceDisconnectionException : BasicException(ErrorCode.WORKSPACE_DISCONNECTION_FAILED) {
}