package com.dcd.server.presentation.domain.application.data.request

open class RunApplicationRequest(
    val langVersion: Int,
    val env: Map<String, String>
)