package com.dcd.server.core.common.command

import com.dcd.server.core.common.command.dto.CommandResult

interface CommandPort {
    fun executeShellCommand(cmd: String): CommandResult
}