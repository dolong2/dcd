package com.dcd.server.presentation.domain.application.data.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class ExecuteCommandRequest(
    @field:NotBlank
    @field:Pattern(regexp = "^(?:(?!(;|\\|\\||&&)|rm\\s+-rf\\s+\\/|(wget|curl)\\s+.*\\|\\s*(sh|bash|zsh|ksh)|cat\\s+/etc/passwd|(cat|grep|awk|sed)\\s+/.*ssh/.*(id_rsa|authorized_keys|known_hosts)).)*$")
    val command: String
)