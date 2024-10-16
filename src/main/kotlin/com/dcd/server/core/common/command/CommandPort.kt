package com.dcd.server.core.common.command

interface CommandPort {
    fun executeShellCommand(cmd: String): Int
    fun executeShellCommandWithResult(cmd: String): List<String>
}