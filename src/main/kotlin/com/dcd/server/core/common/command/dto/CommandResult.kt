package com.dcd.server.core.common.command.dto

data class CommandResult(
    val exitValue: Int,
    val result: List<String>
)
