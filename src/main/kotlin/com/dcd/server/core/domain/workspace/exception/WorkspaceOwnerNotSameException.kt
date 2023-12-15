package com.dcd.server.core.domain.workspace.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class WorkspaceOwnerNotSameException : BasicException(ErrorCode.NOT_SAME_WORKSPACE_OWNER)