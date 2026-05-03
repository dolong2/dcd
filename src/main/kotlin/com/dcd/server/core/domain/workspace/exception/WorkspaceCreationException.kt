package com.dcd.server.core.domain.workspace.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class WorkspaceCreationException : BasicException(ErrorCode.WORKSPACE_CREATION_FAILED) {
}